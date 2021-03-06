package com.logex.headlinenews.ui.news

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.logex.adapter.recyclerview.wrapper.LoadMoreWrapper
import com.logex.fragmentation.anim.DefaultNoAnimator
import com.logex.fragmentation.anim.FragmentAnimator
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.NewsListAdapter
import com.logex.headlinenews.base.MVVMFragment
import com.logex.headlinenews.base.Observer
import com.logex.headlinenews.base.RxBus
import com.logex.headlinenews.model.NewsListEntity
import com.logex.headlinenews.model.SubscribedEntity
import com.logex.headlinenews.model.event.StartBrotherEvent
import com.logex.pullrefresh.listener.PullRefreshListener
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_news_list.*

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻列表页面
 */
class NewsListFragment : MVVMFragment<NewsViewModel>() {
    private var mAdapter: NewsListAdapter? = null
    private var mLoadMoreWrapper: LoadMoreWrapper? = null
    private var lastTime = 0L
    private var mList = arrayListOf<NewsListEntity>()
    private var isLoadMore = false // 加载更多是否触发

    private var mTab: SubscribedEntity? = null

    companion object {

        fun newInstance(args: Bundle): NewsListFragment {
            val fragment = NewsListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun createViewModel(): NewsViewModel = NewsViewModel(context)

    override fun getLayoutId(): Int = R.layout.fragment_news_list

    override fun viewCreate(savedInstanceState: Bundle?) {
        mTab = arguments.getParcelable("tab")

        LogUtil.i("当前标签信息>>>>>>" + GsonUtil.getInstance().toJson(mTab))

        pr_layout.setPullRefreshListener(object : PullRefreshListener() {

            override fun onRefresh() = onPullRefresh()

        })
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator = DefaultNoAnimator()

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        // 获取新闻列表
        mViewModel?.getHomeNewsList(mTab?.category, 20, lastTime)
    }

    override fun onPullRefresh() {
        super.onPullRefresh()
        lastTime = 0
        isLoadMore = false
        // 获取新闻列表
        mViewModel?.getHomeNewsList(mTab?.category, 20, lastTime)
    }

    override fun onLoadMore() {
        super.onLoadMore()
        lastTime = mList[mList.size - 1].behot_time
        isLoadMore = true
        // 获取新闻列表
        mViewModel?.getHomeNewsList(mTab?.category, 20, lastTime)
    }

    override fun dataObserver() {
        super.dataObserver()
        registerObserver(mViewModel?.newsListData, Observer { data ->
            pr_layout.finishRefresh()

            when {
                data != null && data.isNotEmpty() -> {
                    if (isLoadMore) {
                        mList.addAll(data)
                    } else {
                        mList.clear()
                        mList.addAll(data)

                        resetListLoadMore(mLoadMoreWrapper)
                    }

                    showData(mList)
                }
                isLoadMore -> showListEmptyMore(mLoadMoreWrapper)
                else -> {
                    mList.clear()
                    showData(mList)
                }
            }
        })

        registerObserver(mViewModel?.errorData, Observer { errInfo ->
            LogUtil.e("获取新闻列表失败>>>>>>$errInfo")
            pr_layout.finishRefresh()
            showLoadMoreFailed(mLoadMoreWrapper)
        })
    }

    private fun showData(list: List<NewsListEntity>) {
        if (mAdapter == null) {
            mAdapter = NewsListAdapter(context, list, R.layout.recycler_item_news_empty_image)

            //设置布局管理器
            initLinearLayoutManager(rv_news_list, LinearLayoutManager.VERTICAL)

            mLoadMoreWrapper = createLoadMoreWrapper(mAdapter, rv_news_list)

            rv_news_list.adapter = mLoadMoreWrapper

            mAdapter?.setOnItemClickListener({ _, position ->
                val item = mAdapter?.getItem(position)

                // 拼接详情地址
                val itemId = item?.item_id
                val urlSb = StringBuffer("http://m.toutiao.com/i")
                urlSb.append(itemId).append("/info/")
                val url = urlSb.toString() //http://m.toutiao.com/i6412427713050575361/info/

                val bundle = Bundle()
                bundle.putString(NewsDetailFragment.DETAIL_URL, url)
                bundle.putString(NewsDetailFragment.GROUP_ID, item?.group_id.toString())
                bundle.putString(NewsDetailFragment.ITEM_ID, itemId.toString())
                RxBus.getDefault().post(StartBrotherEvent(NewsDetailFragment.newInstance(bundle)))
            })
        } else {
            mLoadMoreWrapper?.notifyDataSetChanged()
        }
    }
}