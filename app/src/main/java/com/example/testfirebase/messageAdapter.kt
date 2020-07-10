import android.content.Context
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
import com.squareup.picasso.Picasso

class messageAdapter(private val context: Context)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

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

    //何してるところかよくわからない
    class messageListItem(val message: Message){}

    //メッセージオブジェクトに関する何か
    private val itemList = mutableListOf<messageListItem>()

    //何を追加するんでしょう？
    fun add(message: Message){
        itemList.add(messageListItem(message))
    }

    //セルが必要になる度に呼び出される
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)

        //ビューを生成
        if(viewType == 0){
            val layout = layoutInflater.inflate(R.layout.chat_me_row,parent,false)
            return messageAdapter.ViewMeHolder(layout)
        }else{
            val layout = layoutInflater.inflate(R.layout.chat_you_row,parent,false)
            return messageAdapter.ViewMeHolder(layout)
        }
    }

    //ViewTypeを返す関数(メッセージ送信者が自分か相手か比較する)
    //[戻り値] 0: 自分 / 1: 相手
    override fun getItemViewType(position: Int): Int {
        //自分のUIDを取得
        val meUid = FirebaseAuth.getInstance().uid
        //IDを比較
        if(itemList[position].message.send_user == meUid){
            return 0
        }else{
            return 1
        }
    }

    //何かをカウントしている関数
    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定する。ここでリスナなどを設定する
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            0->{
                val holder_me = holder as ViewMeHolder
                holder_me.me_msg.text = itemList[position].message.message
                holder_me.me_time.text = itemList[position].message.time.toString()
                Picasso.get().load("https://www.zespri.com/ja-JP/kiwibrothers/cmn/img/about_green_figure.jpg").into(holder_me.me_img)
            }
            1->{
                val holder_you = holder as ViewYouHolder
                holder_you.you_msg.text = itemList[position].message.message
                holder_you.you_time.text = itemList[position].message.time.toString()
                Picasso.get().load("https://www.zespri.com/ja-JP/kiwibrothers/cmn/img/about_gold_figure.jpg").into(holder_you.you_img)
            }
        }
    }

}