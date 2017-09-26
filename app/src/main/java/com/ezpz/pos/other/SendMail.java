package com.ezpz.pos.other;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Belal on 10/30/2015.
 */

//Class is extending AsyncTask because this class is going to perform a networking operation
public class SendMail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;

    //Information to send email
    private String email;
    private String subject;
    private String message;
    private Activity activity;
    private Class activity2;

    //Progressdialog to show while sending email
    private ProgressDialog mProgressDialog;

    //Class Constructor
    public SendMail(Context context, String email, String subject, String message, Activity activity, Class activity2){
        //Initializing variables
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.activity = activity;
        this.activity2 = activity2;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Sending activation link...");
        mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        mProgressDialog.dismiss();
        //Showing a success message
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("Activation link sent");
        builder.setMessage("Please checkout your email to activated your account.");
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.startActivity(new Intent(activity, activity2));
                activity.finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        //Toast.makeText(context,"Confirmation Link Sent. Please checkout your email to activated your account.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "jkt07.dewaweb.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        String email = new Memcache(activity).getMailConfig().getEmail();
                        String password = new Memcache(activity).getMailConfig().getPassword();
                        return new PasswordAuthentication(email, password);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(new Memcache(activity).getMailConfig().getEmail()));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            //Adding subject
            mm.setSubject(subject);
            //Adding message
            mm.setText(message);

            //Sending email
            Transport.send(mm);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
