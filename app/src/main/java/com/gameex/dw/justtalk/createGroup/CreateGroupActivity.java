package com.gameex.dw.justtalk.createGroup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gameex.dw.justtalk.ObjPack.MsgInfo;
import com.gameex.dw.justtalk.R;
import com.gameex.dw.justtalk.chattingPack.GroupChatActivity;
import com.gameex.dw.justtalk.managePack.BaseActivity;
import com.gameex.dw.justtalk.publicInterface.DoneCreateGroupCallBack;
import com.gameex.dw.justtalk.publicInterface.FragmentCallBack;
import com.gameex.dw.justtalk.util.DataUtil;
import com.gameex.dw.justtalk.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

public class CreateGroupActivity extends BaseActivity
        implements View.OnClickListener, FragmentCallBack, DoneCreateGroupCallBack {
    private static final String TAG = "CreateGroupActivity";
    public static CreateGroupActivity sActivity;
    /**
     * 返回箭头
     */
    private ImageView mBack;
    /**
     * 标题
     */
    private TextView mTitle;
    /**
     * 下一步
     */
    private TextView mNext;
    /**
     * fragment manager
     */
    private FragmentManager mFragmentManager;

    private FragmentTransaction mTransaction;
    /**
     * 选择用户的fragment
     */
    private ChooseContactFragment mChooseFragment;
    /**
     * 设置群头像和群名称的fragment
     */
    private DoneCreateFragment mDoneCreateFragment;
    /**
     * 承接userInfo集合的json String
     */
    private String mUserInfosStr;
    /**
     * 承接联系人选择fragment的userInfo集合
     */
    private String mUserChoosedStr;
    /**
     * 群头像
     */
    private Uri mGroupIcon;
    /**
     * 群名称
     */
    private String mGroupName;
    /**
     * 群成员头像
     */
    private List<Uri> mGroupMemberIcons = new ArrayList<>();

    @Override
    public void sendMessage(String value) {
        mUserChoosedStr = value;
    }

    @Override
    public void sendGroupIcon(Uri groupIcon) {
        mGroupIcon = groupIcon;
    }

    @Override
    public void sendGroupName(String groupName) {
        mGroupName = TextUtils.equals(groupName, "添加群名称") ? "" : groupName;
    }

    @Override
    public void sendUris(List<Uri> uris) {
        mGroupMemberIcons = uris;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        sActivity = this;
    }

    /**
     * 初始化界面
     */
    private void initView() {
        setContentView(R.layout.activity_create_group);

        mBack = findViewById(R.id.back_create_group);

        mTitle = findViewById(R.id.title_create_group);

        mNext = findViewById(R.id.done_create_group);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mBack.setOnClickListener(this);

        mTitle.setText("发起群聊");

        mNext.setOnClickListener(this);

        mFragmentManager = getSupportFragmentManager();
        mUserInfosStr = getIntent().getStringExtra("user_infos");
        mChooseFragment = ChooseContactFragment.newInstance(mUserInfosStr);
        showFragment(mChooseFragment);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_create_group:
                if (mNext.getText().equals("完成")) {
                    showFragment(mChooseFragment);
                    mTitle.setText("发起群聊");
                    mNext.setText("下一步");
                } else {
                    finish();
                }
                break;
            case R.id.done_create_group:
                if (mNext.getText().equals("完成")) {
                    goCreateGroup();
                    return;
                }
                mDoneCreateFragment = DoneCreateFragment.newInstance(mUserChoosedStr);
                showFragment(mDoneCreateFragment);
                mTitle.setText("群聊");
                mNext.setText("完成");
                break;
            default:
                break;
        }
    }

    /**
     * 展示fragment
     *
     * @param fragment 需要展示的fragment
     */
    private void showFragment(Fragment fragment) {
        mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.container_create, fragment);
        mTransaction.commit();
    }

    /**
     * 开始创建群组
     */
    private void goCreateGroup() {
        final Intent intent = new Intent();
        final String groupName = TextUtils.isEmpty(mGroupName) ? "未设置群名称" : mGroupName;
        JMessageClient.createPublicGroup(groupName, "这个群主很懒...", new CreateGroupCallback() {
            @Override
            public void gotResult(int i, String s, long groupId) {
                if (i == 0) {
                    LogUtil.d(TAG, "onClick-done_create_group: " + "groupId = " + groupId);
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(CreateGroupActivity.this);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("user_choosed");
                    editor.apply();
                    intent.setClass(CreateGroupActivity.this
                            , GroupChatActivity.class);
                    intent.putExtra("group_icon", mGroupIcon == null
                            ? DataUtil.resourceIdToUri(getPackageName(), R.drawable.icon_group)
                            : mGroupIcon);
                    intent.putExtra("group_name", groupName);
//                    intent.putParcelableArrayListExtra("group_member_icon"
//                            , (ArrayList<? extends Parcelable>) mGroupMemberIcons);
                    intent.putExtra("group_id", groupId);
                    final MsgInfo msgInfo = new MsgInfo();
                    JMessageClient.getGroupInfo(groupId, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, GroupInfo groupInfo) {
                            if (i == 0) {
                                LogUtil.d(TAG, "goCreateGroup: " + "groupInfo = "
                                        + groupInfo.toJson());
                                msgInfo.setGroupInfoJson(groupInfo.toJson());
                            } else {
                                LogUtil.d(TAG, "goCreateGroup: " + "responseCode = "
                                        + i + " ; desc = " + s);
                            }
                        }
                    });
                    intent.putExtra("msg_info", msgInfo);
                    startActivity(intent);
                    finish();
                } else {
                    LogUtil.d(TAG, "onClick-done_create_group: "
                            + "responseCode = " + i + "desc = " + s);
                    Toast.makeText(CreateGroupActivity.this, "创建失败"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mNext.getText().equals("完成")) {
            showFragment(mChooseFragment);
            mTitle.setText("发起群聊");
            mNext.setText("下一步");
        } else {
            super.onBackPressed();
        }
    }
}