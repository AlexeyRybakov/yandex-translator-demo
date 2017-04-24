package com.example.yandextranslatordemo.presentation.event_bus;


import org.greenrobot.eventbus.Subscribe;

public interface MessageReceiver {

    @Subscribe
    void onMessageEvent(OpenTranslation event);

}
