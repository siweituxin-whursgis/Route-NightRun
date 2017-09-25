package com.example.huyigong.route_nightrun;

import org.kymjs.chat.ChatActivity;
import org.kymjs.chat.adapter.ChatAdapter;
import org.kymjs.chat.bean.Message;
import org.kymjs.kjframe.KJActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 定制聊天窗口
 */

public class MyChatActivity extends ChatActivity {

    @Override
    protected void initListView() {
        adapter = new ChatAdapter(this, datas, getOnChatItemClickListener());
        mRealListView.setAdapter(adapter);
    }

    @Override
    protected void createReplayMsg(Message message) {
        final Message reMessage = new Message(message.getType(), Message.MSG_STATE_SUCCESS, "胡奕公", "avatar", "郭元浩", "avatar", "好啊", false, true, new Date());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * (new Random().nextInt(3) + 1));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            datas.add(reMessage);
                            adapter.refresh(datas);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
