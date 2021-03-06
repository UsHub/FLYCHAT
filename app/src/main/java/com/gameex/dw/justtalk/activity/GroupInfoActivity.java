package com.gameex.dw.justtalk.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gameex.dw.justtalk.R;
import com.gameex.dw.justtalk.manage.BaseActivity;
import com.gameex.dw.justtalk.util.DataUtil;
import com.gameex.dw.justtalk.util.DialogUtil;
import com.gameex.dw.justtalk.util.GroupInfoUtil;
import com.gameex.dw.justtalk.util.LogUtil;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.GroupAnnouncement;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import es.dmoral.toasty.Toasty;

import static com.gameex.dw.justtalk.util.DataUtil.CHOOSE_PHOTO;
import static com.gameex.dw.justtalk.util.DataUtil.CROP_PHOTO;
import static com.gameex.dw.justtalk.util.DataUtil.TAKE_PHOTO;

/**
 * 群组详细信息展示
 */
public class GroupInfoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "GroupInfoActivity";
    /**
     * 修改群昵称请求码
     */
    private static final int EDIT_GROUP_NICK_CODE = 101;
    /**
     * 修改我在本群的昵称请求码
     */
    private static final int EDIT_MY_GROUP_NICK_CODE = 102;
    /**
     * 返回箭头
     */
    private ImageView mBack;
    /**
     * 页面标题
     */
    private TextView mTitle;
    /**
     * 右上角功能键
     */
    private ImageView mMore;
    /**
     * 包含头像的layout
     */
    private RelativeLayout mIconLayout;
    /**
     * 我的群昵称的layout
     */
    private RelativeLayout mNickLayout;
    /**
     * 群二维码layout
     */
    private RelativeLayout mQrCodeLayout;
    /**
     * 消息推送开关layout
     */
    private RelativeLayout mPushLayout;
    /**
     * 邀请群成员layout
     */
    private RelativeLayout mInviteMember;
    /**
     * 群头像
     */
    private CircularImageView mIcon;
    /**
     * 群名称
     */
    private TextView mName;
    /**
     * 我的群昵称
     */
    private TextView mNick;
    /**
     * 群公告
     */
    private TextView mNotice;
    /**
     * 群管理
     */
    private TextView mManage;
    /**
     * 群消息通知
     */
    private TextView mPush;
    /**
     * 长时间未领取红包
     */
    private TextView mMoneyGift;
    /**
     * 群聊天记录
     */
    private TextView mChatRecord;
    /**
     * 群聊天文件
     */
    private TextView mChatFile;
    /**
     * 查看群成员
     */
    private RelativeLayout mMember;
    /**
     * 群成员数
     */
    private TextView mMemberCount;
    /**
     * 退出群组
     */
    private TextView mExit;
    /**
     * 群组体
     */
    private GroupInfo mGroupInfo;

    private AlertDialog mDialog;
    private Uri mPhotoUri;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        JMessageClient.registerEventReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }

    /**
     * 初始化布局，笨拙的绑定id，只怪缺少经验
     */
    private void initView() {
        setContentView(R.layout.activity_group);
        mBack = findViewById(R.id.back);
        mTitle = findViewById(R.id.title);
        mMore = findViewById(R.id.more);
        mIconLayout = findViewById(R.id.group_icon_info_layout);
        mIcon = findViewById(R.id.group_icon_info);
        mMember = findViewById(R.id.group_member);
        mName = findViewById(R.id.group_nick);
        mNickLayout = findViewById(R.id.mine_group_nick_layout);
        mNick = findViewById(R.id.mine_group_nick);
        mQrCodeLayout = findViewById(R.id.group_qr_code_layout);
        mNotice = findViewById(R.id.group_notice);
        mManage = findViewById(R.id.group_manage);
        mPushLayout = findViewById(R.id.group_push_layout);
        mPush = findViewById(R.id.group_push_nick);
        mMoneyGift = findViewById(R.id.money_gift_long_time_no_see);
        mChatRecord = findViewById(R.id.group_chat_record);
        mChatFile = findViewById(R.id.group_chat_file);
        mMemberCount = findViewById(R.id.group_member_count);
        mInviteMember = findViewById(R.id.invite_member_layout);
        mExit = findViewById(R.id.exit);
        mExit.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    @SuppressLint("SetTextI18n")
    private void initData() {
        mGroupInfo = GroupInfo.fromJson(getIntent().getStringExtra("group_info"));

        mBack.setOnClickListener(this);
        mTitle.setText("群详情");
        mMore.setOnClickListener(this);

        mIconLayout.setOnClickListener(this);
        mIcon.setOnClickListener(this);
        GroupInfoUtil.initGroupIcon(mGroupInfo, this, mIcon);
        mMember.setOnClickListener(this);
        mName.setText(mGroupInfo.getGroupName());
        mNickLayout.setOnClickListener(this);
        mNick.setText(getGroupNick(JMessageClient.getMyInfo().getUserName()));
        mQrCodeLayout.setOnClickListener(this);
        mNotice.setOnClickListener(this);
        mManage.setOnClickListener(this);
        mPushLayout.setOnClickListener(this);
        if (mGroupInfo.isGroupBlocked() == 1) {
            mPush.setText("已屏蔽");
            mPush.setTextColor(Color.RED);
        }
        mMoneyGift.setOnClickListener(this);
        mChatRecord.setOnClickListener(this);
        mChatFile.setOnClickListener(this);
        List<GroupMemberInfo> memberInfos = mGroupInfo.getGroupMemberInfos();
        if (memberInfos != null && memberInfos.size() > 0) {
            mMemberCount.setText(memberInfos.size() + "");
        } else {
            mMemberCount.setText("0");
        }
        mInviteMember.setOnClickListener(this);
    }

    /**
     * 获取相机权限
     */
    @SuppressLint("CheckResult")
    private void requestPermission() {
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        Intent takePhotoIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                        mPhotoUri = DataUtil.getPhotoUri(this, null, "take_photo.jpg");
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO);
                        mDialog.dismiss();
                    } else {
                        Toasty.warning(this, "Denied permission"
                                , Toasty.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * 在线消息处理事件
     *
     * @param event messageEvent
     */
    public void onEventMainThread(MessageEvent event) {
        Message message = event.getMessage();
        GroupInfo groupInfo = (GroupInfo) message.getTargetInfo();
        if (groupInfo.getGroupID() != mGroupInfo.getGroupID()) {
            return;
        }
        if (message.getContentType() == ContentType.eventNotification) {
            EventNotificationContent content = (EventNotificationContent) message.getContent();
            updateInfo(content);
        }
    }

    /**
     * 接收到群信息变化后，更新当前页内的群信息
     *
     * @param content 群事件对象
     */
    private void updateInfo(EventNotificationContent content) {
        LogUtil.d(TAG, "updateInfo: " + "content = " + content.toJson());
        switch (content.getEventNotificationType()) {
            case group_member_removed:  //群成员被移除
            case group_member_added:    //新成员加群
            case group_member_exit: //群成员退群
                //更新群成员数,若退群的是自己，则结束此界面
                if (content.getUserNames().get(0).equals(JMessageClient.getMyInfo().getUserName()))
                    finish();
                else
                    JMessageClient.getGroupInfo(mGroupInfo.getGroupID(), new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, GroupInfo groupInfo) {
                            if (i == 0) {
                                mGroupInfo = groupInfo;
                                mMemberCount.setText("" + mGroupInfo.getGroupMemberInfos().size());
                            } else {
                                LogUtil.d(TAG, "updateInfo-group_member_removed: "
                                        + "responseCode = " + i + " ;desc = " + s);
                                Toasty.error(GroupInfoActivity.this, "群成员数更新失败").show();
                            }
                        }
                    });
                break;
//            case group_info_updated:    //群信息变化
//                //TODO：更新群信息
//                break;
            default:
                JMessageClient.getGroupInfo(mGroupInfo.getGroupID(), new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, GroupInfo groupInfo) {
                        if (i == 0) {
                            mGroupInfo = groupInfo;
                        } else {
                            LogUtil.d(TAG, "updateInfo-group_member_removed: "
                                    + "responseCode = " + i + " ;desc = " + s);
                            Toasty.error(GroupInfoActivity.this, "群成员数更新失败").show();
                        }
                    }
                });
                break;
        }
    }

    /**
     * 改变图片方式Dialog
     */
    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mDialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_set_photo, null);
        TextView takePhoto = view.findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(this);
        TextView choosePhoto = view.findViewById(R.id.choose_from_lib_text);
        choosePhoto.setOnClickListener(this);
        mDialog.setView(view);
        mDialog.show();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.back: //返回
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.more: //更多
                Toasty.custom(this, "敬请期待", R.drawable.vector_hook_check
                        , R.color.colorBlack, Toasty.LENGTH_SHORT, true
                        , false).show();
                break;
            case R.id.group_icon_info_layout:   //修改群昵称
                intent.setClass(this, EditMyInfoActivity.class);
                intent.putExtra("title", "群昵称");
                intent.putExtra("my_nick", mName.getText());
                startActivityForResult(intent, EDIT_GROUP_NICK_CODE);
                break;
            case R.id.group_icon_info:
                showTypeDialog();   //修改群头像
                break;
            case R.id.group_member: //群组成员
                intent.setClass(this, GroupMemberActivity.class);
                intent.putExtra("groupInfo", mGroupInfo.toJson());
                startActivity(intent);
                break;
            case R.id.mine_group_nick_layout:   //修改我在本群的昵称
                intent.setClass(this, EditMyInfoActivity.class);
                intent.putExtra("title", "我在本群的昵称");
                intent.putExtra("my_nick", mNick.getText());
                startActivityForResult(intent, EDIT_MY_GROUP_NICK_CODE);
                break;
            case R.id.group_qr_code_layout: //展示群二维码
                mGroupInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {
                        LogUtil.d(TAG, "OnClick-group_qr_code_layout: "
                                + "responseCode = " + i + " ;" + s);
                        if (i == 0)
                            DialogUtil.showQrDialog(GroupInfoActivity.this, String.valueOf(mGroupInfo.getGroupID())
                                    , bitmap);
                        else
                            DialogUtil.showQrDialog(GroupInfoActivity.this, String.valueOf(mGroupInfo.getGroupID())
                                    , null);
                    }
                });
                break;
            case R.id.group_notice: //操作群公告
                mGroupInfo.getAnnouncementsByOrder(new RequestCallback<List<GroupAnnouncement>>() {
                    @Override
                    public void gotResult(int i, String s, List<GroupAnnouncement> groupAnnouncements) {
                        if (i == 0) {
                            LogUtil.d(TAG, "onClick-group_notice: "
                                    + "announcements = " + groupAnnouncements);
                        } else {
                            LogUtil.d(TAG, "onClick-group_notice: " + "responseCode = " + i
                                    + " ;desc = " + s);
                        }
                    }
                });
                break;
            case R.id.group_manage: //操作群管理
                intent.setClass(GroupInfoActivity.this, GroupManageActivity.class);
                intent.putExtra("groupId", mGroupInfo.getGroupID());
                startActivity(intent);
                break;
            case R.id.group_push_layout:
                final int block;
                if (mPush.getText().equals("已屏蔽")) {
                    block = 0;
                } else {
                    block = 1;
                }
                mGroupInfo.setBlockGroupMessage(block, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        LogUtil.d(TAG, "onClick-group_push_layout: " + "requestCode = " + i +
                                " ;desc = " + s);
                        if (i == 0) {
                            if (block == 1) {
                                mPush.setText("已屏蔽");
                                mPush.setTextColor(Color.RED);
                            } else {
                                mPush.setText("已开启");
                                mPush.setTextColor(getResources().getColor(R.color.colorAccent));
                            }
                        }
                    }
                });
                break;
            case R.id.money_gift_long_time_no_see:
                //TODO:查看长时间未领取的红包
                break;
            case R.id.group_chat_record:    //查看群聊天记录
                //TODO：查看群聊天记录
                break;
            case R.id.group_chat_file:  //查看群文件
                //TODO: 查看群文件
                break;
            case R.id.invite_member_layout: //邀请群成员
                intent.setClass(GroupInfoActivity.this, SearchUserActivity.class);
                intent.putExtra("groupId", mGroupInfo.getGroupID());
                startActivity(intent);
                break;
            case R.id.exit:
                JMessageClient.exitGroup(mGroupInfo.getGroupID(), new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Toasty.info(GroupInfoActivity.this, "已退出").show();
                        } else {
                            Toasty.info(GroupInfoActivity.this, "出错了，请重试").show();
                            LogUtil.d(TAG, "OnClick-exit: " + "responseCode = " + i + " ;desc = " + s);
                        }
                    }
                });
                break;
            case R.id.take_photo:
                requestPermission();
                break;
            case R.id.choose_from_lib_text:
                Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK, null);
                choosePhotoIntent.setDataAndType(MediaStore.Images
                        .Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO);
                mDialog.dismiss();
                break;
        }
    }

    /**
     * 获取用户在该群组的群昵称
     *
     * @param username 用户名
     * @return string
     */
    private String getGroupNick(String username) {
        GroupMemberInfo memberInfo = mGroupInfo.getGroupMember(username, null);
        return memberInfo.getNickName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode
            , @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    DataUtil.cropPhoto(this, null, mPhotoUri);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    DataUtil.cropPhoto(this, null, data.getData());
                }
                break;
            case CROP_PHOTO:    //群头像
                if (data != null) {
                    Bundle extras = data.getExtras();
                    assert extras != null;
                    mBitmap = extras.getParcelable("data");
                    if (mBitmap != null) {
                        File file = DataUtil.setPicToView(this, mBitmap, "user_icon.jpg");
                        mGroupInfo.updateAvatar(file, "group_icon", new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                LogUtil.d(TAG, "onActivityResult-CROP_PHOTO: " +
                                        "responseCode = " + i + " ;desc = " + s);
                                Toasty.info(GroupInfoActivity.this, "正在上传头像"
                                        , Toasty.LENGTH_SHORT, true).show();
                                if (i == 0) {
                                    mIcon.setImageBitmap(mBitmap);
                                    Toasty.success(GroupInfoActivity.this, "头像上传成功"
                                            , Toasty.LENGTH_SHORT, true).show();
                                } else {
                                    LogUtil.d(TAG, "onActivityResult-CROP_PHOTO: "
                                            + "responseCode = " + i + " ;desc = " + s);
                                    Toasty.error(GroupInfoActivity.this, "头像上传失败"
                                            , Toasty.LENGTH_SHORT, true).show();
                                }
                            }
                        });
                    }
                }
                break;
            case EDIT_GROUP_NICK_CODE:  //群昵称
                if (data != null && resultCode == RESULT_OK) {
                    String nick = data.getStringExtra("my_nick");
                    mGroupInfo.updateName(nick, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                GroupInfoUtil.initGroupIcon(mGroupInfo, GroupInfoActivity.this, mIcon);
                                mName.setText(nick);
                            } else {
                                LogUtil.d(TAG, "onActivityResult-EDIT_GROUP_NICK_CODE: "
                                        + "responseCode = " + i + " ;desc = " + s);
                                Toasty.error(GroupInfoActivity.this, "群昵称更新失败"
                                        , Toasty.LENGTH_SHORT, true).show();
                            }
                        }
                    });
                }
                break;
            case EDIT_MY_GROUP_NICK_CODE:   //我的群名称
                if (data != null && resultCode == RESULT_OK) {
                    String myNick = data.getStringExtra("my_nick");
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    mGroupInfo.setMemNickname(myInfo.getUserName(), null, myNick, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                mNick.setText(myNick);
                            } else {
                                LogUtil.d(TAG, "onActivityResult-EDIT_MY_GROUP_NICK_CODE: "
                                        + "responseCode = " + i + " ;desc = " + s);
                                Toasty.error(GroupInfoActivity.this, "我的群昵称更新失败"
                                        , Toasty.LENGTH_SHORT, true).show();
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
