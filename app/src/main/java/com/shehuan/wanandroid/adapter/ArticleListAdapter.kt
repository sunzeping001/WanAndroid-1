package com.shehuan.wanandroid.adapter

import android.content.Context
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.othershe.baseadapter.ViewHolder
import com.othershe.baseadapter.base.CommonBaseAdapter
import com.shehuan.wanandroid.R
import com.shehuan.wanandroid.bean.article.DatasItem

class ArticleListAdapter(context: Context?, data: List<DatasItem>?, isOpenLoadMore: Boolean) :
        CommonBaseAdapter<DatasItem>(context, data, isOpenLoadMore) {
    override fun getItemLayoutId(): Int {
        return R.layout.rv_item_article_layout
    }

    override fun convert(viewHolder: ViewHolder, data: DatasItem, position: Int) {
        viewHolder.setText(R.id.articleTitleTv, Html.fromHtml(data.title).toString())
        val typeTv = viewHolder.getView<TextView>(R.id.articleTypeTv)
        val collectTv = viewHolder.getView<ImageView>(R.id.articleCollectIv)
        if (data.collect) {
            collectTv.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_like_fill))
        } else {
            collectTv.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_like))
        }

        if (data.tags.isNotEmpty()) {
            typeTv.text = data.tags[0].name
            typeTv.visibility = View.VISIBLE
        } else {
            typeTv.visibility = View.GONE
        }
        viewHolder.setText(R.id.articleAuthorTv, data.author)
        viewHolder.setText(R.id.articleTimeTv, data.niceDate)
    }
}