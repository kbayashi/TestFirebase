import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testfirebase.Message
import com.example.testfirebase.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class messageAdapter(private val context: Context)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //自分のUIDを取得
    val meUid = FirebaseAuth.getInstance().uid

    //1行で使用する各部品（ビュー）を保持したもの(自分)
    class ViewMeHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val me_img: ImageView = itemView.findViewById(R.id.chat_me_row_pic_imageView)
        val me_msg: Button = itemView.findViewById(R.id.chat_me_row_msg_button)
        val me_time: TextView = itemView.findViewById(R.id.chat_me_row_time_textView)
    }

    //1行で使用する各部品（ビュー）を保持したもの(相手)
    class ViewYouHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val you_img: ImageView = itemView.findViewById(R.id.chat_you_row_pic_imageView)
        val you_msg: Button = itemView.findViewById(R.id.chat_you_row_msg_button)
        val you_time: TextView = itemView.findViewById(R.id.chat_you_row_time_textView)
    }

    //画像(自分)
    class ViewMeImageHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val me_img: ImageView = itemView.findViewById(R.id.chat_me_row_pic_imageView)
        val me_time: TextView = itemView.findViewById(R.id.chat_me_row_time_textView)
        val me_msg: Button = itemView.findViewById(R.id.chat_me_row_msg_button)
        val me_imgSrc: ImageView = itemView.findViewById(R.id.chat_me_row_imgSrc)
    }

    //画像(相手)
    class ViewYouImageHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val you_img: ImageView = itemView.findViewById(R.id.chat_you_row_pic_imageView)
        val you_time: TextView = itemView.findViewById(R.id.chat_you_row_time_textView)
        val you_msg: Button = itemView.findViewById(R.id.chat_you_row_msg_button)
        val you_imgSrc: ImageView = itemView.findViewById(R.id.chat_you_row_imgSrc)
    }

    //何してるところかよくわからない
    class messageListItem(val message: Message)

    //メッセージオブジェクトに関する何か
    private val itemList = mutableListOf<messageListItem>()

    //メッセージの追加
    fun add(message: Message){
        itemList.add(messageListItem(message))
    }

    //セルが必要になる度に呼び出される
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)

        //ビューを生成
        if(viewType == 0){
            val layout = layoutInflater.inflate(R.layout.chat_me_row,parent,false)
            return ViewMeHolder(layout)
        }else if(viewType == 1){
            val layout = layoutInflater.inflate(R.layout.chat_you_row,parent,false)
            return ViewYouHolder(layout)
        }else if(viewType == 2){
            val layout = layoutInflater.inflate(R.layout.chat_me_row,parent,false)
            return ViewMeImageHolder(layout)
        }else{
            val layout = layoutInflater.inflate(R.layout.chat_you_row,parent,false)
            return ViewYouImageHolder(layout)
        }
    }

    //ViewTypeを返す関数(メッセージ送信者が自分か相手か比較する)
    //[戻り値] 0: 自分(メッセージ) / 1: 相手(メッセージ) / 2: 画像) / 3: 相手(画像)
    override fun getItemViewType(position: Int): Int {
        //IDを比較
        if(itemList[position].message.send_user == meUid){
            // 自分
            if(itemList[position].message.image_flag == false) {
                // メッセージ
                return 0
            } else {
                // 画像
                return 2
            }

        }else{
            // 相手
            if (itemList[position].message.image_flag == false) {
                // メッセージ
                return 1
            } else {
                // 画像
                return 3
            }
        }
    }

    //何かをカウントしている関数
    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定する。ここでリスナなどを設定する
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //データベースインスタンスを作成
        val db = FirebaseFirestore.getInstance()

        when(holder.itemViewType){
            0->{
                val holder_me = holder as ViewMeHolder
                holder_me.me_msg.text = itemList[position].message.message
                holder_me.me_time.text = itemList[position].message.sendTimestampToString(itemList[position].message.time)
                //自分のユーザアイコンを取りに行く
                val docRef = db.collection("user").document(itemList[position].message.send_user)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                            Picasso.get().load(document.getString("img")).into(holder_me.me_img)
                        } else {
                            Log.d(TAG, "No such document")
                            Picasso.get().load("https://cv.tipsfound.com/windows10/02014/8.png").into(holder_me.me_img)
                        }
                    }
            }
            1->{
                val holder_you = holder as ViewYouHolder
                holder_you.you_msg.text = itemList[position].message.message
                holder_you.you_time.text = itemList[position].message.sendTimestampToString(itemList[position].message.time)
                //相手のユーザアイコンを取りに行く
                val docRef = db.collection("user").document(itemList[position].message.send_user)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                            Picasso.get().load(document.getString("img")).into(holder_you.you_img)
                        } else {
                            Log.d(TAG, "No such document")
                            Picasso.get().load("https://cv.tipsfound.com/windows10/02014/8.png").into(holder_you.you_img)
                        }
                    }
            }
            2 ->{
                val holder_me = holder as ViewMeImageHolder
                holder_me.me_time.text = itemList[position].message.sendTimestampToString(itemList[position].message.time)
                // 自分のユーザアイコンを取りに行く
                db.collection("user").document(itemList[position].message.send_user).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                            Picasso.get().load(document.getString("img")).into(holder_me.me_img)
                        } else {
                            Log.d(TAG, "No such document")
                            Picasso.get().load("https://cv.tipsfound.com/windows10/02014/8.png").into(holder_me.me_img)
                        }
                    }
                // Viewを表示、非表示
                holder_me.me_msg.visibility = View.GONE
                holder_me.me_imgSrc.visibility = View.VISIBLE
                // 画像を読み込む
                Picasso.get().load(itemList[position].message.message).into(holder_me.me_imgSrc)
            }
            3 ->{
                val holder_you = holder as ViewYouImageHolder
                holder_you.you_time.text = itemList[position].message.sendTimestampToString(itemList[position].message.time)
                //相手のユーザアイコンを取りに行く
                db.collection("user").document(itemList[position].message.send_user).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                            Picasso.get().load(document.getString("img")).into(holder_you.you_img)
                        } else {
                            Log.d(TAG, "No such document")
                            Picasso.get().load("https://cv.tipsfound.com/windows10/02014/8.png").into(holder_you.you_img)
                        }
                    }
                // Viewを表示、非表示
                holder_you.you_msg.visibility = View.GONE
                holder_you.you_imgSrc.visibility = View.VISIBLE
                // 画像を読み込む
                Picasso.get().load(itemList[position].message.message).into(holder_you.you_imgSrc)
            }
        }
    }

}