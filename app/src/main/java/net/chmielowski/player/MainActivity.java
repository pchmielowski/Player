package net.chmielowski.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.IOException;

class MainActivity extends AppCompatActivity {


    private MediaPlayer player = new MediaPlayer();
    private AudioManager manager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(
                        intent.getAction())) {
                    pause();
                }
            }
        }, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));


        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(
                    "https://s3.eu-central-1.amazonaws"
                            + ".com/net-chmielowski-test-bucket-1/Angels.mp3"
                            + ".wav"
            );
            player.setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            findViewById(R.id.play).setVisibility(View.VISIBLE);
                        }
                    });
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        findViewById(R.id.play).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        play();
                    }
                });
        findViewById(R.id.pause).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pause();
                    }
                });
    }

    private void pause() {
        player.pause();
    }

    private void play() {
        int result = manager.requestAudioFocus(
                new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int it) {
                        if (it == AudioManager.AUDIOFOCUS_LOSS ||
                                it == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                                it
                                        == AudioManager
                                        .AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                            pause();
                        } else {
                            player.start();
                        }

                    }

                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
        );
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            player.start();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
}
