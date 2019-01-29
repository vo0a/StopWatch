package darom.stopwatch

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.timer

// 화면이 하나인 간단한 앱이라서 이 프로젝트에서는 Anko 라이브러리를 사용하지 않습니다.
//대신 FloatingActionButton 이라는 둥근 형태의 버튼을 사용하는데 벡터 드로어블 이미지를 사용하므로
//모듈 수즌의 그레이들 파일에서 벡터 드로어블 하휘 호환 설정을 해줘야 합니다.
class MainActivity : AppCompatActivity() {

    private var time = 0 //시간을 계산할 변수를 0 으로 초기화 선언합니다.
    private var isRunning = false
    private var timerTask: Timer? = null  //타이머를 취소하려면 timer를 실행하고 반환되는 Timer 객체를 변수에 저장해둘 필요가 있습니다.
    //이를 위해 timerTask 변수를 null을 허용하는 Timer 타입으로 선언했습니다.
    private var lap = 1 //몇 번째 랩인지를 표시하고자 변수 lap을 1로 초기화하여 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener{
            isRunning = !isRunning //FAB이 클릭되면 동작 중인지를 저장하는 isRunning 변수의 값을 반전시키고

            // 그 상태에 따라서 타이머를 시작 또는 일시정지 시킵니다.
            if(isRunning){
                start()
            } else{
                pause()
            }
        }

        lapButton.setOnClickListener {
            recordLapTime()
        }

        resetFab.setOnClickListener{
            reset()
        }

    }


    private fun pause(){
        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp) //타이머 시작과 반대로 FAB를 클릭하면 시작 이미지로 교체합니다.
        timerTask?.cancel() // 실행 중인 타이머가 있다면 타이머를 취소합니다.
    }


    private fun start(){
        fab.setImageResource(R.drawable.ic_pause_black_24dp) //타이머 시작하는 FAB을 누르면 일시정지 이미지로 변경

        //안드로이드에는 UI를 조작하는 메인 스레드와 오래 걸리는 작업을 보이지 않는 곳에서 처리하는 워커 스레드가 존재합니다.
        //timer는 워커 스레드로 UI를 조작할 수 없습니다. 따라서 runOnUiThread() 메서드를 사용해야 합니다.
        timerTask = timer(period = 10){
            time++ //0.01 초마다 이 변수를 증가시키면서 UI를 갱신합니다.

            val sec = time / 100
            val milli = time % 100

            //위에서 계산한 초와 밀리초를 각각의 텍스트 뷰에 설정합니다.
            runOnUiThread{
                secTextView.text = "$sec"
                milliTextView.text = "$milli"
            }
        }
    }

    /*
    LinearLayout에 동적으로 뷰를 추가하기

    1. TextView 객체를 동적으로 생성하여 LinearLayout의 위에서부터 아래로 쌓습니다. 최근의 랩 타입이 맨 위로 오게 해야 합니다.
        addView() 메서드를 사용하여 두 번째 인자에 추가할 인덱스값을 지정하면 해당 위치에 뷰가 추가됩니다.
    val textView = TextView(this)
    textView.text = "글자"
    lapLayout.addView(textView)

    2. 다음은 항상 맨 위(0)에 텍스트 뷰를 추가하는 코드입니다.
     lapLayout.addView(textView, 0)

    */
    //랩 타임 버튼을 클릭하면
    private fun recordLapTime(){
        val lapTime = this.time //현재 시간을 지역 변수에 저장하고
        val textView = TextView(this) //동적으로 TextView를 생성하여 텍스트 값을 '1 LAB : 5.35'와 같은 형태가 되도록 시간을 계산하여 문자열로 설정합니다.
        textView.text = "$lap LAB : ${lapTime / 100}.${lapTime % 100}"

        //맨 위에 랩타입 추가
        lapLayout.addView(textView, 0)
        lap++
    }

    private fun reset(){
        timerTask?.cancel() //실행 중인 타이머가 있다면 취소한다

        //모든 변수 초기화
        time = 0
        isRunning = false
        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        secTextView.text = "0"
        milliTextView.text = "00"

        //모든 랩타임 제거
        lapLayout.removeAllViews()
        lap = 1
    }

}
