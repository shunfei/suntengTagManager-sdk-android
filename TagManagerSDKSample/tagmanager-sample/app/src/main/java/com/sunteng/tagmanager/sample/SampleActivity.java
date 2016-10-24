package com.sunteng.tagmanager.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sunteng.tagmanager.sdk.Container;
import com.sunteng.tagmanager.sdk.TagManager;

import java.text.SimpleDateFormat;
import java.util.Map;

public class SampleActivity extends AppCompatActivity implements View.OnClickListener {

    final String FUNCTION_NAME_1 = "func1";
    final String FUNCTION_NAME_2 = "func2";
    final String FUNC_CALL_MACRO_FUNCTION_NAME = "funcName";

    private Container mContainer;
    private TextView tv_container_info;
    private EditText et_event_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        mContainer = ContainerLoaderSingleton.getContainerLoader().getContainer();
        initView();
        initFuncCallback();
    }

    private void initFuncCallback() {
        mContainer.registerFunctionCallTagCallback(FUNCTION_NAME_1, tagCallback);
        mContainer.registerFunctionCallTagCallback(FUNCTION_NAME_2, tagCallback);
        mContainer.registerFunctionCallMacroCallback(FUNC_CALL_MACRO_FUNCTION_NAME, macroCallback);
    }

    private void initView() {
        findViewById(R.id.bt_fire_event).setOnClickListener(this);
        findViewById(R.id.bt_refresh).setOnClickListener(this);
        tv_container_info = (TextView) findViewById(R.id.tv_container_info);
        et_event_name = (EditText) findViewById(R.id.et_event_name);
        et_event_name.setText("hello");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showContainerInfo();
    }

    private void showContainerInfo() {
        String date = SimpleDateFormat.getDateInstance().format(new java.util.Date (mContainer.getLastRefreshTime()));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ContainerId：")
                .append(mContainer.getContainerId())
                .append('\n')
                .append("ContainerVersion：")
                .append(mContainer.getVersion())
                .append('\n')
                .append("Last refresh time： ").append(date)
                .append('\n')
                .append("应用 ID： ")
                .append(mContainer.getString("{{应用 ID}}")); //Container.getString()方法通过宏的名称可以获取内容
        tv_container_info.setText(stringBuilder.toString());
    }

    // 执行容器中Tag设置的方法，根据对应方法名进行回调，参数为tag中设置的键值对参数
    Container.FunctionCallTagCallback tagCallback = new Container.FunctionCallTagCallback() {
        @Override
        public void execute(String functionName, Map<String, Object> parameterMap) {
            // Not UI Thread
            Log.i("SampleActivity","execute functionName: " + functionName);
            switch (functionName){ // 根据不同的functionName，进行不同的处理逻辑
                case FUNCTION_NAME_1:
                    //在管理后台配置tag的键值对参数，可以在parameterMap获取
                    Log.i("SampleActivity","tag parameter = " + parameterMap.toString());
                    break;
                case FUNCTION_NAME_2:
                    //方法调用类型的宏执行后的返回值=func2
                    Log.i("SampleActivity","execute funcName getValue = func2, execute tag!");

                    //此时从parameterMap获取是tag中设置的键值对参数
                    Log.i("SampleActivity","tag parameter = " + parameterMap.toString());

                    //可以注册其他的方法
                    mContainer.registerFunctionCallTagCallback("func3", tagCallback);
                    break;
                case "func3":
                    Log.i("SampleActivity","execute func3 success!");
                    break;
                default:break;
            }

            final String exeFunctionName = functionName;
            // 可以去更新UI内容
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SampleActivity.this, "execute " + exeFunctionName , Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    // 执行方法调用宏中设置的方法, 根据对应方法名进行回调，参数为方法调用宏中设置的键值对参数，返回参数可用来调用另一个tag中设置的方法
    Container.FunctionCallMacroCallback macroCallback = new Container.FunctionCallMacroCallback() {
        @Override
        public Object getValue(String functionName, Map<String, Object> parameterMap) {
            // Not UI Thread
            Log.i("SampleActivity","getValue functionName: " + functionName);
            if (FUNC_CALL_MACRO_FUNCTION_NAME.equals(functionName)){
                //此时从parameterMap获取是macro中设置的键值对参数
                Log.i("SampleActivity","tag parameter = " + parameterMap.toString());
                final String exeFunctionName = functionName;
                // 可以去更新UI内容
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SampleActivity.this, exeFunctionName + "getValue() return func2", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return "func2"; //返回func2，即方法调用宏的value被赋值为func2，而宏作为后台管理中代码(tag)设置的方法名，所以之后会执行该tag。(execute func2)
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_refresh:
                ContainerLoaderSingleton.getContainerLoader().refresh();
                showContainerInfo();
                break;
            case R.id.bt_fire_event:
                if (!TextUtils.isEmpty(et_event_name.getText())){
                    TagManager.getInstance(SampleActivity.this).getTGDataLayer().pushEvent(et_event_name.getText().toString());
                    break;
                }
                Toast.makeText(SampleActivity.this, "fire event name is empty " , Toast.LENGTH_SHORT).show();
                break;
            default:break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 可以在方法不需要时进行反注册，可以避免可能出现的内存泄露
        mContainer.unRegisterFunctionCallTagCallback(FUNCTION_NAME_1);
        mContainer.unRegisterFunctionCallTagCallback(FUNCTION_NAME_2);
        mContainer.unRegisterFunctionCallTagCallback("func3");
        mContainer.unRegisterFunctionCallMacroCallback(FUNC_CALL_MACRO_FUNCTION_NAME);
    }
}
