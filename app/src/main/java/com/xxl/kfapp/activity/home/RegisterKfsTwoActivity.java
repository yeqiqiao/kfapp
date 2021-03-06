package com.xxl.kfapp.activity.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.xxl.kfapp.R;
import com.xxl.kfapp.adapter.ProgressAdapter;
import com.xxl.kfapp.base.BaseActivity;
import com.xxl.kfapp.model.response.ApplyStatusVo;
import com.xxl.kfapp.model.response.ProgressVo;
import com.xxl.kfapp.utils.PreferenceUtils;
import com.xxl.kfapp.utils.Urls;
import com.xxl.kfapp.widget.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import talex.zsw.baselibrary.util.klog.KLog;

/**
 * 作者：XNN
 * 日期：2017/6/7
 * 作用：注册快发师第二步 审核
 */

public class RegisterKfsTwoActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.mTitleBar)
    TitleBar mTitleBar;
    @Bind(R.id.next)
    Button next;
    @Bind(R.id.pRecyclerView)
    RecyclerView pRecyclerView;
    @Bind(R.id.tv_checking)
    TextView tvChecking;
    @Bind(R.id.tv_fixedreason)
    TextView tvFixedReason;
    @Bind(R.id.tv_customreason)
    TextView tvCustomReason;
    @Bind(R.id.lyt_reason_shsb)
    TextView lytReasonShsb;
    @Bind(R.id.tv_tips)
    TextView tvTips;

    private ProgressAdapter progressAdapter;
    private List<ProgressVo> progressVos;
    private ApplyStatusVo statusVo;
    private Drawable pass, fair;

    @Override
    protected void initArgs(Intent intent) {
        statusVo = (ApplyStatusVo) intent.getSerializableExtra("");
    }

    @Override
    protected void initView(Bundle bundle) {
        setContentView(R.layout.activity_registerkfs_two);
        ButterKnife.bind(this);
        next.setOnClickListener(this);
        mTitleBar.setTitle("注册快发师申请");
        mTitleBar.setBackOnclickListener(this);
        initDrawables();
        switch (statusVo.getChecksts()) {
            case "0":
                next.setClickable(false);
                next.setBackgroundResource(R.drawable.bg_corner_gray);
                next.setTextColor(getResources().getColor(R.color.gray));
                break;
            case "1":
                tvChecking.setText("，您的初审已通过");
                tvChecking.setCompoundDrawables(pass, null, null, null);
                break;
            case "2":
                tvChecking.setText("真遗憾，您的初审未通过");
                tvChecking.setCompoundDrawables(fair, null, null, null);
                lytReasonShsb.setVisibility(View.VISIBLE);
                tvFixedReason.setText(statusVo.getFixedreason());
                tvCustomReason.setText(statusVo.getCustomreason());
                next.setText("重新填写");
                tvTips.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void initData() {
        initInfoRecycleView();
    }

    private void initDrawables() {
        pass = getDrawable(R.mipmap.sh_cg);
        fair = getDrawable(R.mipmap.sh_sb);
        pass.setBounds(0, 0, pass.getMinimumWidth(), pass.getMinimumHeight());
        fair.setBounds(0, 0, fair.getMinimumWidth(), fair.getMinimumHeight());
    }

    private void doUpdateApplyStatus() {
        String token = PreferenceUtils.getPrefString(getAppApplication(), "token", "1234567890");
        OkGo.<String>get(Urls.baseUrl + Urls.updateApplyStatus)
                .tag(this)
                .params("token", token)
                .params("applysts", "12")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject json = new JSONObject(response.body());
                            String code = json.getString("code");
                            if (!code.equals("100000")) {
                                sweetDialog(json.getString("msg"), 1, false);
                            } else {
                                KLog.i(response.body());
                                startActivity(new Intent(RegisterKfsTwoActivity.this, RegisterKfsThreeActivity.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                if (statusVo != null) {
                    switch (statusVo.getChecksts()) {
                        case "0":
                            ToastShow("请等待审核通过");
                            break;
                        case "1":
                            doUpdateApplyStatus();
                            break;
                        case "2":
                            Intent intent = new Intent(RegisterKfsTwoActivity.this, RegisterKfsOneActivity.class);
                            intent.putExtra("isRepeat", true);
                            startActivity(intent);
                            finish();
                            break;
                    }
                }
                break;
        }

    }

    /**
     * 初始化progress列表
     */
    private void initInfoRecycleView() {
        progressAdapter = new ProgressAdapter(new ArrayList<ProgressVo>(), getScrnWeight() / 4);
        pRecyclerView.setAdapter(progressAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        pRecyclerView.setLayoutManager(layoutManager);
        setData();
        //        pRecyclerView.smoothScrollToPosition();

    }

    private void setData() {
        progressVos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ProgressVo vo = new ProgressVo();
            if (i == 0) {
                vo.setName("申请加盟");
                vo.setTag(2);
            } else if (i == 1) {
                vo.setName("审核");
                vo.setTag(1);
            } else if (i == 2) {
                vo.setName("阅读协议");
            } else if (i == 3) {
                vo.setName("考试");
            } else if (i == 4) {
                vo.setName("申请成功");
            }

            progressVos.add(vo);
        }
        progressAdapter.setNewData(progressVos);
    }
}
