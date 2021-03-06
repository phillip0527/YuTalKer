package com.im.yutalker.push.fragments.main;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.im.yutalker.common.app.PresenterFragment;
import com.im.yutalker.common.widget.EmptyView;
import com.im.yutalker.common.widget.PortraitView;
import com.im.yutalker.common.widget.recycler.RecyclerAdapter;
import com.im.yutalker.factory.model.dp.Session;
import com.im.yutalker.factory.presenter.message.SessionContract;
import com.im.yutalker.factory.presenter.message.SessionPresenter;
import com.im.yutalker.push.R;
import com.im.yutalker.push.activities.MessageActivity;
import com.im.yutalker.utils.DateTimeUtil;

import butterknife.BindView;
import butterknife.OnClick;


public class ActiveFragment extends PresenterFragment<SessionContract.Presenter> implements SessionContract.View {
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    // 适配器,Session可以直接从数据库查询
    private RecyclerAdapter<Session> mAdapter;

    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        // 初始化Recycler
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter<Session>() {
            @Override
            protected int getItemViewType(int position, Session session) {
                return R.layout.cell_chat_list;
            }

            @Override
            protected ViewHolder<Session> onCreateViewHolder(View root, int viewType) {
                return new ActiveFragment.ViewHolder(root);
            }
        });
        // 点击事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                // 跳转到聊天界面
                MessageActivity.show(getContext(), session);
            }
        });

        // 初始化占位布局
        mEmptyView.bind(mRecyclerView);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        // 进行一次数据加载
        mPresenter.start();
    }

    @Override
    protected SessionContract.Presenter initPresenter() {
        return new SessionPresenter(this);
    }

    @Override
    public RecyclerAdapter<Session> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        placeHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    // 界面的数据渲染
    class ViewHolder extends RecyclerAdapter.ViewHolder<Session> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_content)
        TextView mContent;
        @BindView(R.id.txt_time)
        TextView mTime;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Session session) {
            mPortraitView.setup(Glide.with(ActiveFragment.this), session.getPicture());
            mName.setText(session.getTitle());
            mContent.setText(TextUtils.isEmpty(session.getContent()) ? "" : session.getContent());
            mTime.setText(DateTimeUtil.getSimpleDate(session.getModifyAt()));
        }

    }

}
