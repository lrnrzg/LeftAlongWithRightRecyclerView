package com.zhu.leftalongwithrightrecyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.zhu.leftalongwithrightrecyclerview.httpbean.HostMenuBean;
import com.zhu.leftalongwithrightrecyclerview.httpbean.HttpResponseBean;
import com.zhu.leftalongwithrightrecyclerview.httpbean.SubMenuBean;
import com.zhu.leftalongwithrightrecyclerview.rv.RecyclerViewAdapter;
import com.zhu.leftalongwithrightrecyclerview.rv.RightDetailRVAdapter;
import com.zhu.leftalongwithrightrecyclerview.rv.RightMenuBean;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DoubleLinkedRecylcerView extends LinearLayout {

    private Context mContext;

    private RecyclerView mRecyclerViewHost;
    private RecyclerView mRecyclerViewSub;
    private RecyclerViewAdapter mHostAdapter;
    private RightDetailRVAdapter mSubAdapter;
    private LinearLayoutManager mLinearLayoutManagerSub;

    public DoubleLinkedRecylcerView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public DoubleLinkedRecylcerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        init();
    }

    public DoubleLinkedRecylcerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        mContext = context;
        init();
    }

    public DoubleLinkedRecylcerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    private  void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.double_rv_layout, this, true);

        initHostRecyclerView();
        initSubRecyclerView();
    }

    private void initSubRecyclerView() {
        mRecyclerViewSub = findViewById(R.id.rv_right);
        mSubAdapter = new RightDetailRVAdapter(R.layout.rv_item);
        mRecyclerViewSub.setAdapter(mSubAdapter);
        mLinearLayoutManagerSub = new LinearLayoutManager(mContext);
        mRecyclerViewSub.setLayoutManager(mLinearLayoutManagerSub);
    }

    private void initHostRecyclerView() {
        mRecyclerViewHost = findViewById(R.id.rv_left);
        mHostAdapter = new RecyclerViewAdapter(R.layout.rv_item, this);
        mRecyclerViewHost.setAdapter(mHostAdapter);
        LinearLayoutManager linearLayoutManagerLeft = new LinearLayoutManager(mContext);
        mRecyclerViewHost.setLayoutManager(linearLayoutManagerLeft);
    }


    private HttpResponseBean mHttpResponseBean;

    public void setData(HttpResponseBean responseBean) {
        mHttpResponseBean = responseBean;

        List<String> leftMenus = getHostMenuData(responseBean);
        mHostAdapter.setNewData(leftMenus);

        List<RightMenuBean> subMenuBean = getSubMenuBean(responseBean);
        mSubAdapter.setNewData(subMenuBean);
    }

    @NotNull
    private List<String> getHostMenuData(HttpResponseBean responseBean) {
        List<HostMenuBean> hostMenuBeans = responseBean.getCategoryOneArray();
        List<String> leftMenus = new ArrayList<>();
        for (int i = 0; i < hostMenuBeans.size(); i++) {
            leftMenus.add(hostMenuBeans.get(i).getName());
        }
        return leftMenus;
    }

    private List<RightMenuBean> getSubMenuBean(HttpResponseBean responseBean) {
        List<RightMenuBean> subMenuBeans = new ArrayList<>();
        List<HostMenuBean> hostMenuBeans = responseBean.getCategoryOneArray();
        for (int i = 0; i < hostMenuBeans.size(); i++) {
            RightMenuBean rightMenuBeanHead = new RightMenuBean();
            rightMenuBeanHead.setTitle(true);
            rightMenuBeanHead.setName(hostMenuBeans.get(i).getName());
            subMenuBeans.add(rightMenuBeanHead);
            List<SubMenuBean> categoryTwoArray = hostMenuBeans.get(i).getCategoryTwoArray();
            for (int j = 0; j < categoryTwoArray.size(); j++) {
                RightMenuBean rightMenuBeanContent = new RightMenuBean();
                rightMenuBeanContent.setTitle(false);
                rightMenuBeanContent.setName(categoryTwoArray.get(j).getName());
                subMenuBeans.add(rightMenuBeanContent);
            }
        }
        return subMenuBeans;
    }


    public void scrollSubMenu(int position) {
        int count = 0;
        for (int i = 0; i < position; i++) {
            count += mHttpResponseBean.getCategoryOneArray().get(i).getCategoryTwoArray().size();
        }
        count += position;

        mRecyclerViewSub.stopScroll();
        smoothMoveToPosition(count);
    }


    private void smoothMoveToPosition(int n) {
        int firstItem = mLinearLayoutManagerSub.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManagerSub.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerViewSub.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerViewSub.getChildAt(n - firstItem).getTop();
            mRecyclerViewSub.scrollBy(0, top);
        } else {
            mRecyclerViewSub.scrollToPosition(n);
        }
    }
}
