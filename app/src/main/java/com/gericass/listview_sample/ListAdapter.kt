package com.gericass.listview_sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.gericass.listview_sample.model.Article


// ListViewのAdapter
class ListAdapter(private val context: Context) : BaseAdapter() {
    var articles: List<Article> = emptyList()

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = articles.size

    override fun getItem(position: Int): Any? = articles[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val (viewHolder, view) = if (convertView == null) {
            // 初回時
            // リスト表示するXMLを選択
            val contentView = layoutInflater.inflate(R.layout.content_list, parent, false)
            // 画像、タイトル、ユーザー名のViewを取得
            val container = contentView.findViewById(R.id.content_container) as ConstraintLayout
            val image = contentView.findViewById(R.id.image) as ImageView
            val title = contentView.findViewById(R.id.title) as TextView
            val username = contentView.findViewById(R.id.username) as TextView
            val viewHolder = ViewHolder(container, image, title, username)
            viewHolder to contentView
        } else {
            // 再利用時
            convertView.tag as? ViewHolder to convertView
        }
        val item = getItem(position) as Article
        viewHolder?.let {
            // 画像をセット
            Glide
                .with(context)
                .load(item.user.profile_image_url)
                .centerCrop()
                .into(viewHolder.imageView)
            // タイトルをセット
            viewHolder.title.text = item.title
            // ユーザー名をセット
            viewHolder.username.text = item.user.name
            // タップした時の挙動をセット
            viewHolder.container.setOnClickListener {
                Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    // パフォーマンス向上のため、一度生成したビューを使い回す
    inner class ViewHolder(
        val container: ConstraintLayout,
        val imageView: ImageView,
        val title: TextView,
        val username: TextView
    )

}