package com.example.testfirebase

class createGroupAdapter internal constructor(private var rowDataList: List<RowData>) : RecyclerView.Adapter<MainViewHolder>(){

    /**
     * ViewHolder作るメソッド
     * 最初しか呼ばれない。
     * ここでViewHolderのlayoutファイルをインフレーとして生成したViewHolderをRecyclerViewに返す。
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_main, parent, false)
        return MainViewHolder(view)
    }

    /**
     * ViewHolderとRecyclerViewをバインドする
     * 一行のViewに対して共通でやりたい処理をここで書く。今回はテキストのセットしかしてないけど。
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val rowData = rowDataList[position]
        holder.hogeTitle.text = rowData.hogeTitle
        holder.hogeContents.text = rowData.hogeContents
    }

    /**
     * リストの行数
     * @return
     */
    override fun getItemCount(): Int {
        return rowDataList.size
    }
}

class MainViewHolder(itemView: View) : ViewHolder(itemView) {
    var hogeTitle: TextView = itemView.findViewById(R.id.hoge_title_text_view)
    var hogeContents: TextView = itemView.findViewById(R.id.hoge_contents_text_view)

}