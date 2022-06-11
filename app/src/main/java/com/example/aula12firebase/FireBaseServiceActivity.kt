package com.example.aula12firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//tem que estender essa classe
class FireBaseServiceActivity :FirebaseMessagingService() {

    //6. tem que declarar no manifest, se não, também não vai enviar menssagens Tem que colocar dentro de aplication o serviço

    //1. método que vai receber a menssagem do firebase no background
    override fun onMessageReceived(message: RemoteMessage) {  //verrificação
        super.onMessageReceived(message)
        val notification = message.notification // vai chamar o método passando a notificação, passando a remoteMessage
        showNotification(notification)
    }

    //5.se for um android com a versão maior ou igual a 26 tem que criar o NotificationChannel, se não, não vai criar as menssagens
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ChannelIdMessage"
            val descriptionText = "ChannelDescription"
            val importance = NotificationManager.IMPORTANCE_DEFAULT  // Importancia padrão
            val channel = NotificationChannel("ChannelIdMessage", name, importance).apply {
                // tem que passar os parametros com a variáveis, e vai dar um apply recebendo a descrição
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    //2. método para mostrar a notificação, só que dessa vez com a notificação, por isso o .Notification
    private fun showNotification(notification: RemoteMessage.Notification?) {
        val title = notification?.title ?: ""  // está pegando o título da menssagem
        val message = notification?.body ?: ""  //está pegando o corpo da menssagem
        val intent = Intent(this, MainActivity::class.java) // Nessa Intente que muda de activity e da para escolher para qual activity vai querer ir
        val pendingIntent: PendingIntent? = // esse pendingIntente vai testar a versão do aplicativo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  //se for maior ou igual a versão S
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE) // vai receber a FLAG_MUTABLE
            } else {
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT) // se não for uma versão maior ou igual a S, vai pedir para fazer o UPDATE
            }

        // 3. Builder é para criar o Channel da menssagem
        val builder = NotificationCompat.Builder(this, "ChannelIdMessage")
        createNotificationChannel()
        val mNotification = builder // abaixo está criando os parametros
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setContentText(message)
            .setAutoCancel(true) //esse é para pegar a menssagem e arrastar para o lado, se não ela vai ficar presa
            .build()

        //4.
        val notificationManager = //passando o contexto
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, mNotification)
    }

    //6. tem que declarar no manifest, se não, também não vai enviar menssagens. Tem que colocar dentro de aplication o serviço

}