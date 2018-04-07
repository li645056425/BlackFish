package top.omooo.blackfish.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import top.omooo.blackfish.MallPagerActivity.ClassifyGoodsActivity;
import top.omooo.blackfish.MallPagerActivity.SearchActivity;
import top.omooo.blackfish.R;
import top.omooo.blackfish.adapter.GeneralVLayoutAdapter;
import top.omooo.blackfish.bean.BannerInfo;
import top.omooo.blackfish.bean.GridInfo;
import top.omooo.blackfish.bean.MallGoodsInfo;
import top.omooo.blackfish.bean.MallGoodsItemInfo;
import top.omooo.blackfish.bean.MallPagerInfo;
import top.omooo.blackfish.bean.UrlInfoBean;
import top.omooo.blackfish.listener.OnNetResultListener;
import top.omooo.blackfish.utils.AnalysisJsonUtil;
import top.omooo.blackfish.utils.OkHttpUtil;
import top.omooo.blackfish.view.CustomToast;
import top.omooo.blackfish.view.RecycleViewBanner;

/**
 * Created by Omooo on 2018/2/25.
 */

public class MallFragment extends BaseFragment {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private Context mContext;

    private Toolbar mToolbar;
    private ImageView mImageMenu, mImageMsg;
    private RelativeLayout mHeaderLayout;

    private RecycleViewBanner mBanner;
    private List<BannerInfo> mBannerInfos;

    final List<DelegateAdapter.Adapter> adapters = new LinkedList<>();
    private static final String TAG = "MallFragment";
    private VirtualLayoutManager layoutManager;
    private RecyclerView.RecycledViewPool viewPool;
    private DelegateAdapter delegateAdapter;

    private SimpleDraweeView mImageGridItem;
    private TextView mTextGridItem;

    private AnalysisJsonUtil mJsonUtil = new AnalysisJsonUtil();
    private List<MallPagerInfo> mMallPagerInfos;

    public static MallFragment newInstance() {
        return new MallFragment();
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_mall_layout;
    }

    @Override
    public void initViews() {

        getActivity().getWindow().setStatusBarColor(Color.parseColor("#00000000"));
        mToolbar = findView(R.id.toolbar_mall);
        mToolbar.getBackground().setAlpha(0);

        mContext = getActivity();
        mRecyclerView = findView(R.id.rv_fragment_mall_container);
        mRefreshLayout = findView(R.id.swipe_container);

        mImageMenu = findView(R.id.iv_mall_header_menu);
        mImageMsg = findView(R.id.iv_mall_header_msg);
        mHeaderLayout = findView(R.id.rl_mall_header_layout);

        layoutManager = new VirtualLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);

        viewPool = new RecyclerView.RecycledViewPool();
        mRecyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 20);

        delegateAdapter = new DelegateAdapter(layoutManager, false);
        mRecyclerView.setAdapter(delegateAdapter);

        addItemViews();
    }

    private void addItemViews() {
        //轮播图
        SingleLayoutHelper bannerHelper = new SingleLayoutHelper();
        GeneralVLayoutAdapter bannerAdapter = new GeneralVLayoutAdapter(mContext, bannerHelper, 1) {

            @Override
            public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MainViewHolder(LayoutInflater.from(mContext).inflate(R.layout.mall_pager_banner_layout, parent,false));
            }

            @Override
            public void onBindViewHolder(MainViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                mBanner = holder.itemView.findViewById(R.id.rvb_mall_header);
                mBannerInfos = new ArrayList<>();
                mBannerInfos.add(new BannerInfo("https://i.loli.net/2018/04/07/5ac867c069199.png"));
                mBannerInfos.add(new BannerInfo("https://i.loli.net/2018/04/07/5ac8373ebdbc1.png"));
                mBannerInfos.add(new BannerInfo("https://i.loli.net/2018/04/07/5ac866b99d3f6.png"));
                mBannerInfos.add(new BannerInfo("https://i.loli.net/2018/04/07/5ac86862086bf.png"));
                mBannerInfos.add(new BannerInfo("https://i.loli.net/2018/04/07/5ac868e88e912.png"));
                mBannerInfos.add(new BannerInfo("https://i.loli.net/2018/04/07/5ac86939d7db6.jpg"));
                mBanner.setRvBannerData(mBannerInfos);
                mBanner.setOnSwitchRvBannerListener(new RecycleViewBanner.OnSwitchRvBannerListener() {
                    @Override
                    public void switchBanner(int position, SimpleDraweeView simpleDraweeView) {
                        simpleDraweeView.setScaleType(ImageView.ScaleType.FIT_START);
                        simpleDraweeView.setImageURI(mBannerInfos.get(position).getUrl());
                    }
                });
                mBanner.setOnBannerClickListener(new RecycleViewBanner.OnRvBannerClickListener() {
                    @Override
                    public void onClick(int position) {

                    }
                });
            }
        };
        adapters.add(bannerAdapter);

        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(5);
        GeneralVLayoutAdapter gridAdapter = new GeneralVLayoutAdapter(mContext, gridLayoutHelper, 10){
            @Override
            public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MainViewHolder(LayoutInflater.from(mContext).inflate(R.layout.mall_pager_two_line_grid, parent, false));
            }

            @Override
            public void onBindViewHolder(MainViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                mImageGridItem = holder.itemView.findViewById(R.id.iv_mall_grid_item);
                mTextGridItem = holder.itemView.findViewById(R.id.tv_mall_grid_item);

            }
        };
        delegateAdapter.setAdapters(adapters);
    }

    @Override
    public void initListener() {
        mImageMenu.setOnClickListener(this);
        mImageMsg.setOnClickListener(this);
        mHeaderLayout.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mJsonUtil = new AnalysisJsonUtil();
        mMallPagerInfos = new ArrayList<>();
        OkHttpUtil.getInstance().startGet(UrlInfoBean.mallGoodsUrl, new OnNetResultListener() {
            @Override
            public void onSuccessListener(String result) {
                mMallPagerInfos = mJsonUtil.getDataFromJson(result, 3);

                List<BannerInfo> bannerInfos = mMallPagerInfos.get(0).getBannerInfos();
                Log.i(TAG, "onSuccessListener: bannerInfos     " + bannerInfos.size());

                List<GridInfo> gridInfos = mMallPagerInfos.get(0).getClassifyInfos();
                Log.i(TAG, "onSuccessListener: gridInfos     " + gridInfos.size());

                String singleImageUrl = mMallPagerInfos.get(0).getSingleImageUrl();
                Log.i(TAG, "onSuccessListener: singleImageUrl     " + singleImageUrl);

                List<BannerInfo> goodsInfos = mMallPagerInfos.get(0).getGridGoodsInfos();
                Log.i(TAG, "onSuccessListener: goodsInfos     " + goodsInfos.size());

                List<MallGoodsInfo> mallGoodsInfos = mMallPagerInfos.get(0).getMallGoodsInfos();
                Log.i(TAG, "onSuccessListener: mallGoodsInfos     " + mallGoodsInfos.size());
                List<MallGoodsItemInfo> mallGoodsItemInfos = mallGoodsInfos.get(2).getMallGoodsItemInfos();
                Log.i(TAG, "onSuccessListener: mallGoodsItemInfos     " + mallGoodsItemInfos.size());
            }

            @Override
            public void onFailureListener(String result) {
                Log.i(TAG, "onFailureListener: "+result);
            }
        });
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()) {
            case R.id.iv_mall_header_menu:
                startActivity(new Intent(mContext, ClassifyGoodsActivity.class));
                break;
            case R.id.iv_mall_header_msg:
                CustomToast.show(mContext, "消息中心");
                break;
            case R.id.rl_mall_header_layout:
                startActivity(new Intent(mContext, SearchActivity.class));
                break;
            default:break;
        }
    }
}
