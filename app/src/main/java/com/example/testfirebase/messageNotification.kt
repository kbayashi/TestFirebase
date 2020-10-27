package com.example.testfirebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class PushNotificationListenerService: FirebaseMessagingService() {

    private val TAG = javaClass.simpleName

    // 新しいトークンが生成された時の処理
    // サーバにトークンを送信する処理などを定義
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG,"onNewToken token:$p0")

        //サーバにトークンを送信する
        sendRegistrationToServer(p0)
        // チャンネルidを設定
        addChannelId()
    }

    //既存のトークンを取得する際の処理
    override fun onCreate() {
        val pushToken = FirebaseInstanceId.getInstance().instanceId
        pushToken.addOnSuccessListener { instanceId ->
            val token = instanceId.token
            Log.d(TAG,"push token：$token")

            // サーバにトークンを送信する処理など
            Log.d(TAG,"onNewToken token:$token")
            // チャンネルidを設定
            addChannelId()
        }
    }

    // 通知を受信したときの処理
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG,"onMessageReceived token:${message?.data}")

        // 今回は通知からタイトルと本文を取得
        val title: String = message?.notification?.title.toString()
        val text: String = message?.notification?.body.toString()

        // 通知表示
        sendNotification(title, text)
    }

    // 通知表示 および 見た目の設定
    private fun sendNotification(title: String, text: String) {
        // 通知タップ時に遷移するアクティビティを指定
        val intent = Intent(this, ChatActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // 何度も遷移しないようにする（1度だけ！）
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_ONE_SHOT)

        // 通知メッセージのスタイルを設定（改行表示に対応）
        val inboxStyle = NotificationCompat.InboxStyle()
        val messageArray = text.split("\n")
        for (msg in messageArray) {
            inboxStyle.addLine(msg)
        }

        // 通知音の設定
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 通知の見た目を設定
        val notificationBuilder
                = NotificationCompat.Builder(this, resources.getString(R.string.default_notification_channel_id))
            .setContentTitle(title)
            .setContentText(text)
            // ステータスバーに表示されるアイコン
            .setSmallIcon(R.drawable.icon_front)
            //通知エリアに表示されるアイコン
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.application_icon))
            // 上で設定したpendingIntentを設定
            .setContentIntent(pendingIntent)
            //メッセージをまとめるときに表示するテキスト
            .setStyle(inboxStyle.setSummaryText("more"))
            //タップしたら自動で閉じるようにする.
            .setAutoCancel(true)
            // 優先度を最大化
            .setPriority(PRIORITY_MAX)
            // 通知音を出すように設定
            .setSound(defaultSoundUri)

        // 通知を実施
        // UUIDを付与することで各通知をユニーク化
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 通知エリアに複数表示させるためランダムのIDを設定
        val uuid = UUID.randomUUID().hashCode()
        notificationManager.notify(uuid, notificationBuilder.build())

        // Android 8.0 以上はチャンネル設定 必須
        // strings.xml に channel_id を指定してください
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(resources.getString(R.string.default_notification_channel_id))
        }
    }

    // チャンネルの設定
    private fun addChannelId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // ヘッドアップ通知を出す場合はチャンネルの重要度を最大にする必要がある
            val channel = NotificationChannel(
                resources.getString(R.string.default_notification_channel_id),
                resources.getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )

            // ロック画面における表示レベル
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            // チャンネル登録
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendRegistrationToServer(p0:String?){
        Log.d(TAG,"sendRegistrationTokenToServer($p0)")
    }
}