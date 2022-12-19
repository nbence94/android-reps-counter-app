package nbey.bens.counterproject;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    FloatingActionButton upSleepDecrease, upSleepIncrease, repeatDecrease, repeatIncrease;
    FloatingActionButton roundDecrease, roundIncrease, pauseDecrease, pauseIncrease;
    TextView upSleepText, repeatText, roundText, pauseText;
    TextView minuteText, secondsText;
    Button controlButton;
    ImageView reverseButton;
    Context context = this;

    //Section One - Sleep & Repeat values
    float main_up_sleep_value = 0;
    int main_repeat_value = 0;

    //Section Two - Round & Pause values
    int main_round_value = 1;
    int main_pause_value = 0;
    int rounds;
    int counter = 0;
    boolean position = false; //if it is false, then it's up
    boolean reversed = false; //Need because of the Signs (Which PositionButton be shown for first)

    //Section Four - Classes & Objects
    CountDownTimer cdt;
    MediaPlayer soundEffect;
    Dialog counterDialog;

    //Section Five - Time Convert
    ConvertTime ct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_activity);

        InitializeGui();

        //Default values
        upSleepText.setText(String.valueOf(main_up_sleep_value));
        repeatText.setText(String.valueOf(main_repeat_value));
        roundText.setText(String.valueOf(main_round_value));
        pauseText.setText(String.valueOf(main_pause_value));

        //Converting
        ct = new ConvertTime();

        //Control the buttons
        repeatMethod();
        upSleepMethod();
        roundMethod();
        pauseMethod();

        //ReverseButton
        reverse();

        controlButton.setOnClickListener(click -> {
            if(main_up_sleep_value == 0.0 || /*down_sleep_value == 0.0 || */main_repeat_value == 0.0) return;

            Log.i("ControlButton", "Counting has started");

            //Set the control button's color (Unnecessary in fact, but it's fine)
            changeButtonColor(R.color.red);
            controlButton.setText(R.string.control_button_stop);

            //Start preparation timer
            countDownMethod(5, "KÉSZÜLJ FEL!");

            rounds = main_round_value;

            //Prevent the screen goes dark
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        });

    }

    private void exerciseTimer(float goal, float up_tick) {
        //Dialog initialize - configurations --- start
        counterDialog = new Dialog(this);
        counterDialog.setContentView(R.layout.counter_layout);
        counterDialog.setCanceledOnTouchOutside(false);

        TextView repetitionText = counterDialog.findViewById(R.id.counter_value_gui);
        repetitionText.setText("0");

        ImageView toUpSignButton = counterDialog.findViewById(R.id.up_sign_gui);
        ImageView toDownSignButton = counterDialog.findViewById(R.id.down_sign_gui);

        toDownSignButton.setBackgroundResource(R.color.main_font);
        toUpSignButton.setBackgroundResource(R.color.main_font);

        ImageView closeDialogButton = counterDialog.findViewById(R.id.counter_close_gui);

        TextView roundTitle = counterDialog.findViewById(R.id.counter_round_text_gui);
        String remainingRounds = String.valueOf(rounds);
        roundTitle.setText(remainingRounds);

        ProgressBar progressCircle = counterDialog.findViewById(R.id.progress_circle_gui);
        progressCircle.setProgress(counter);

        closeDialogButton.setOnClickListener(v -> {
            resetCounter();
            progressCircle.setProgress(counter);
        });

        counterDialog.show();
        //Dialog --- end

        //Total goal is:
        goal = goal * 1000 * up_tick * 2;
        up_tick = up_tick * 1000;
        Log.i("Timer", "Time:" + up_tick + " :: Goal:" + goal);
        cdt = new CountDownTimer((int)goal, (int)up_tick) {
            @Override
            public void onTick(long time) {
                if(!reversed) {
                    if(position) {
                        toDownSignButton.setBackgroundResource(R.color.main_font);
                        toUpSignButton.setBackgroundResource(R.drawable.button_background_color_up);
                    } else {
                        toUpSignButton.setBackgroundResource(R.color.main_font);
                        toDownSignButton.setBackgroundResource(R.drawable.button_background_color_down);
                    }
                } else {
                    if(position) {
                        toUpSignButton.setBackgroundResource(R.color.main_font);
                        toDownSignButton.setBackgroundResource(R.drawable.button_background_color_down);
                    } else {
                        toDownSignButton.setBackgroundResource(R.color.main_font);
                        toUpSignButton.setBackgroundResource(R.drawable.button_background_color_up);
                    }
                }

                if(position) {
                    position = false;

                    //Push the counter
                    counter++;
                    float multi = (float) (100.0 / main_repeat_value); //The full circle is always 100. I have to count the pieces of it
                    repetitionText.setText(String.valueOf(counter));
                    progressCircle.setProgress((int) (counter * multi));

                    //Sound
                    if(counter == main_repeat_value) {
                        soundEffect = MediaPlayer.create(context, R.raw.phase_sound);
                    } else {
                        soundEffect = MediaPlayer.create(context, R.raw.up_sound);
                    }

                } else {
                    position = true;

                    //Sound
                    soundEffect = MediaPlayer.create(context, R.raw.down_sound);
                }


                soundEffect.start();
                soundEffect.setOnCompletionListener(MediaPlayer::release);

            }

            @Override
            public void onFinish() {
                if(rounds > 1) {
                    resetCounter();
                    countDownMethod(main_pause_value, "SZÜNET");
                    rounds--;
                } else {
                    resetCounter();
                }
            }
        };
        cdt.start();
    }

    private void countDownMethod(int countFrom, String title) {
        // CounterDialog - start
        Dialog prepareDialog = new Dialog(this);
        prepareDialog.setContentView(R.layout.get_ready_layout);
        prepareDialog.setCanceledOnTouchOutside(false);

        TextView counterText = prepareDialog.findViewById(R.id.prepare_counter_text_gui);
        counterText.setText(String.valueOf(countFrom));

        TextView titleText = prepareDialog.findViewById(R.id.prepare_title_text_gui);
        titleText.setText(title);

        prepareDialog.show();

        //CounterDialog - end
        long totalPauseValue = (long) (countFrom + 1) * 1000;
        // +1 because I want it to go to 0
        cdt = new CountDownTimer(totalPauseValue, 1000) {
            int counter = countFrom;

            @Override
            public void onTick(long time) {
                String counterValue = counter == 0 ? "GO" : String.valueOf(counter);
                counterText.setText(counterValue);

                if(counter <= 3 && counter > 0) {
                    soundEffect = MediaPlayer.create(context, R.raw.prepare_sound);
                    soundEffect.start();
                    soundEffect.setOnCompletionListener(MediaPlayer::release);
                }

                if(counter == 0) {
                    soundEffect = MediaPlayer.create(context, R.raw.start_end_sound);
                    soundEffect.start();
                    soundEffect.setOnCompletionListener(MediaPlayer::release);
                }

                counter--;
            }

            @Override
            public void onFinish() {
                prepareDialog.dismiss();
                exerciseTimer(main_repeat_value, main_up_sleep_value);

                //Prevent the screen goes dark
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        cdt.start();
    }

    private void changeButtonColor(int color) {
        controlButton.setBackgroundColor(getResources().getColor(color));
    }

    private void resetCounter() {
        //Stop the counting - reset everything

        //Set everything false and 0
        position = false;
        counter = 0;

        //Set the Start button's color and text
        changeButtonColor(R.color.main_step_button_1);
        controlButton.setText(R.string.control_button_start);

        //Stop cooldown timer
        cdt.cancel();

        //Stop preventing the screen time
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Close myDialog
        counterDialog.dismiss();

        Log.i("CounterDialog", "Counting stops");
    }

    private void InitializeGui() {
        upSleepDecrease = findViewById(R.id.up_decrease_btn_gui);
        upSleepIncrease = findViewById(R.id.up_increase_btn_gui);
        upSleepText = findViewById(R.id.up_second_text_gui);

        repeatDecrease = findViewById(R.id.reps_decrease_btn_gui);
        repeatIncrease = findViewById(R.id.reps_increase_btn_gui);
        repeatText = findViewById(R.id.reps_text_gui);

        roundDecrease = findViewById(R.id.round_decrease_btn_gui);
        roundIncrease = findViewById(R.id.round_increase_btn_gui);
        roundText = findViewById(R.id.round_text_gui);

        pauseDecrease = findViewById(R.id.rest_decrease_btn_gui);
        pauseIncrease = findViewById(R.id.rest_increase_btn_gui);
        pauseText = findViewById(R.id.rest_second_text_gui);

        controlButton = findViewById(R.id.control_counter_btn_gui);

        minuteText = findViewById(R.id.minute_summary_gui);
        secondsText = findViewById(R.id.seconds_summary_gui);
        reverseButton = findViewById(R.id.reverse_count_gui);
    }

    private void reverse() {
        reverseButton.setOnClickListener(click -> {
            if(!reversed) {
                reversed = true;
                Log.i("ReverseButton", "True");
                reverseButton.setColorFilter(getResources().getColor(R.color.main_step_button_1));
            } else {
                reversed = false;
                Log.i("ReverseButton", "False");
                reverseButton.setColorFilter(getResources().getColor(R.color.main_font));
            }
        });
    }

    private void repeatMethod() {


        repeatDecrease.setOnClickListener(click -> {
            main_repeat_value -= (main_repeat_value > 0) ? 2 : 0;
            repeatText.setText(String.valueOf(main_repeat_value));
            int summary = ct.sumSeconds(main_up_sleep_value, main_repeat_value, main_round_value, main_pause_value);
            minuteText.setText(ct.getMinutes(summary));
            secondsText.setText(ct.getSeconds(summary));
        });

        repeatIncrease.setOnClickListener(click -> {
            main_repeat_value += (main_repeat_value < 100) ? 2 : 0;
            repeatText.setText(String.valueOf(main_repeat_value));
            int summary = ct.sumSeconds(main_up_sleep_value, main_repeat_value, main_round_value, main_pause_value);
            minuteText.setText(ct.getMinutes(summary));
            secondsText.setText(ct.getSeconds(summary));
        });
    }

    private void upSleepMethod() {
        upSleepDecrease.setOnClickListener(click -> {
            main_up_sleep_value -= (main_up_sleep_value > 0) ? 0.5 : 0;
            upSleepText.setText(String.valueOf(main_up_sleep_value));
            int summary = ct.sumSeconds(main_up_sleep_value, main_repeat_value, main_round_value, main_pause_value);
            minuteText.setText(ct.getMinutes(summary));
            secondsText.setText(ct.getSeconds(summary));
        });

        upSleepIncrease.setOnClickListener(click -> {
            main_up_sleep_value += (main_up_sleep_value < 5) ? 0.5 : 0;
            upSleepText.setText(String.valueOf(main_up_sleep_value));
            int summary = ct.sumSeconds(main_up_sleep_value, main_repeat_value, main_round_value, main_pause_value);
            minuteText.setText(ct.getMinutes(summary));
            secondsText.setText(ct.getSeconds(summary));
        });
    }

    private void roundMethod() {
        roundDecrease.setOnClickListener(click -> {
            main_round_value -= (main_round_value > 1) ? 1 : 0;
            roundText.setText(String.valueOf(main_round_value));
            int summary = ct.sumSeconds(main_up_sleep_value, main_repeat_value, main_round_value, main_pause_value);
            minuteText.setText(ct.getMinutes(summary));
            secondsText.setText(ct.getSeconds(summary));
        });

        roundIncrease.setOnClickListener(click -> {
            main_round_value += (main_round_value < 10) ? 1 : 0;
            roundText.setText(String.valueOf(main_round_value));
            int summary = ct.sumSeconds(main_up_sleep_value, main_repeat_value, main_round_value, main_pause_value);
            minuteText.setText(ct.getMinutes(summary));
            secondsText.setText(ct.getSeconds(summary));
        });
    }

    private void pauseMethod() {
        pauseDecrease.setOnClickListener(click -> {
            main_pause_value -= (main_pause_value > 0) ? 5 : 0;
            pauseText.setText(String.valueOf(main_pause_value));
            int summary = ct.sumSeconds(main_up_sleep_value, main_repeat_value, main_round_value, main_pause_value);
            minuteText.setText(ct.getMinutes(summary));
            secondsText.setText(ct.getSeconds(summary));
        });

        pauseIncrease.setOnClickListener(click -> {
            main_pause_value += (main_pause_value < 60) ? 5 : 0;
            pauseText.setText(String.valueOf(main_pause_value));
            int summary = ct.sumSeconds(main_up_sleep_value, main_repeat_value, main_round_value, main_pause_value);
            minuteText.setText(ct.getMinutes(summary));
            secondsText.setText(ct.getSeconds(summary));
        });
    }

}
