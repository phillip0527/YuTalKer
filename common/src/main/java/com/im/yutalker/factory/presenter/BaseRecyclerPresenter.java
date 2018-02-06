package com.im.yutalker.factory.presenter;


import android.os.Handler;
import android.os.Looper;
import android.support.v7.util.DiffUtil;

import com.im.yutalker.common.widget.recycler.RecyclerAdapter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

/**
 * 对RecyclerView进行一个Presenter封装
 * Created by Phillip on 2018/2/3.
 */

public class BaseRecyclerPresenter<ViewModel, View extends BaseContract.RecyclerView> extends BasePresenter<View> {
    public BaseRecyclerPresenter(View view) {
        super(view);
    }

    /**
     * 刷新数据到界面中
     *
     * @param dataList 新数据
     */
    protected void refreshData(final List<ViewModel> dataList) {
        // 切换主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                View view = getView();
                if (view == null)
                    return;
                // 基本的更新数据并刷新界面
                RecyclerAdapter<ViewModel> adapter = view.getRecyclerAdapter();
                adapter.replace(dataList);
                view.onAdapterDataChanged();
            }
        });
    }

    /**
     * 刷新界面操作，必须在主线程
     *
     * @param diffResult 一个比较差异的结果集
     * @param dataList   具体的新数据
     */
    protected void refreshData(final DiffUtil.DiffResult diffResult, final List<ViewModel> dataList) {
        // 切换主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 主线程运行时
                refreshDataOnUiThread(diffResult, dataList);
            }
        });
    }


    private void refreshDataOnUiThread(final DiffUtil.DiffResult diffResult, final List<ViewModel> dataList) {
        View view = getView();
        if (view == null)
            return;
        // 基本的更新数据并刷新界面
        RecyclerAdapter<ViewModel> adapter = view.getRecyclerAdapter();
        // 改变数据集合并不通知刷新界面
        adapter.getItems().clear();
        adapter.getItems().addAll(dataList);
        // 通知界面刷新占位布局
        view.onAdapterDataChanged();

        // 进行增量更新
        diffResult.dispatchUpdatesTo(adapter);
    }
}
