package com.shehuan.wanandroid.ui.home

import android.support.v7.widget.LinearLayoutManager
import com.shehuan.wanandroid.R
import com.shehuan.wanandroid.adapter.ArticleListAdapter
import com.shehuan.wanandroid.base.fragment.BaseMvpFragment
import com.shehuan.wanandroid.base.net.exception.ResponseException
import com.shehuan.wanandroid.bean.BannerBean
import com.shehuan.wanandroid.bean.article.ArticleBean
import com.shehuan.wanandroid.widget.DivideItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*
import android.view.LayoutInflater
import com.shehuan.wanandroid.bean.article.DatasItem
import com.shehuan.wanandroid.ui.article.ArticleActivity
import com.shehuan.wanandroid.utils.ToastUtil
import com.youth.banner.Banner
import com.shehuan.wanandroid.widget.BannerImageLoader
import com.youth.banner.BannerConfig


class HomeFragment : BaseMvpFragment<HomePresenterImpl>(), HomeContract.View {
    private var pageNum: Int = 0
    private lateinit var articleListAdapter: ArticleListAdapter
    private lateinit var collectDataItem: DatasItem
    private var collectPosition: Int = 0

    private lateinit var bannerBeans: List<BannerBean>
    private lateinit var banner: Banner

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun initPresenter(): HomePresenterImpl {
        return HomePresenterImpl(this)
    }

    override fun loadData() {
        presenter.getBannerData()
        presenter.getArticleList(pageNum)
    }

    override fun initLayoutResID(): Int {
        return R.layout.fragment_home
    }

    override fun initData() {

    }

    override fun initView() {
        banner = LayoutInflater.from(context).inflate(R.layout.home_banner_layout, homeRootLayout, false) as Banner
        banner.setImageLoader(BannerImageLoader())
        banner.setDelayTime(3000)
        banner.setIndicatorGravity(BannerConfig.RIGHT)
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
        banner.setOnBannerListener {
            ArticleActivity.start(mContext, bannerBeans[it].title, bannerBeans[it].url)
        }

        articleListAdapter = ArticleListAdapter(context, null, true)
        articleListAdapter.setLoadingView(R.layout.rv_loading_layout)
        articleListAdapter.setLoadEndView(R.layout.rv_load_end_layout)
        articleListAdapter.setLoadFailedView(R.layout.rv_load_failed_layout)
        // 添加banner
        articleListAdapter.addHeaderView(banner)

        articleListAdapter.setOnItemClickListener { _, data, _ ->
            ArticleActivity.start(mContext, data.title, data.link)
        }
        articleListAdapter.setOnItemChildClickListener(R.id.articleCollectIv) { _, data, position ->
            collectDataItem = data
            collectPosition = position
            if (!data.collect) {
                presenter.collect(data.id)
            } else {
                presenter.uncollect(data.id)
            }
        }
        articleListAdapter.setOnLoadMoreListener {
            presenter.getArticleList(pageNum)
        }
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        articleRv.layoutManager = linearLayoutManager
        articleRv.addItemDecoration(DivideItemDecoration())
        articleRv.adapter = articleListAdapter
    }

    override fun onBannerSuccess(data: List<BannerBean>) {
        bannerBeans = data
        val images = arrayListOf<String>()
        val titles = arrayListOf<String>()

        for (bannerBean in data) {
            images.add(bannerBean.imagePath)
            titles.add(bannerBean.title)
        }

        banner.setImages(images)
        banner.setBannerTitles(titles)
        banner.start()
    }

    override fun onBannerError(e: ResponseException) {

    }

    override fun onArticleListSuccess(data: ArticleBean) {
        if (pageNum == 0) {
            articleListAdapter.setNewData(data.datas)
        } else {
            articleListAdapter.setLoadMoreData(data.datas)
        }
        pageNum++
        if (pageNum == data.pageCount) {
            articleListAdapter.loadEnd()
            return
        }
    }

    override fun onArticleListError(e: ResponseException) {
        articleListAdapter.loadFailed()
    }

    override fun onCollectSuccess(data: String) {
        collectDataItem.collect = true
        articleListAdapter.change(collectPosition + 1)
        ToastUtil.showToast(mContext, "收藏成功")
    }

    override fun onCollectError(e: ResponseException) {

    }

    override fun onUncollectSuccess(data: String) {
        collectDataItem.collect = false
        articleListAdapter.change(collectPosition + 1)
        ToastUtil.showToast(mContext, "取消收藏成功")
    }

    override fun onUncollectError(e: ResponseException) {

    }
}
