package com.example.test;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.annotations.SerializedName;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.lang.Thread;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView DATA; //Отображается в меню

    //Инициализация ретрофита и интерфейса работы с ним
    APIInterface api= getClient().create(APIInterface.class);;
    private static Retrofit retrofit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DATA = (TextView) findViewById(R.id.DATA);

        //
        //Запуск цикла проверки погоды. Информация обновляется периодически.
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    getWeather(); //Вызов метода получающего информацию о погоде
                    try
                    {
                        Thread.sleep(50000); //Приостановление обновления
                    }
                    catch (InterruptedException e)
                    {
                    }
                }

            }
        });
        myThread.start();
    }
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
    //Создание метода работы с ретрофитом
    public static Retrofit getClient()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create()) //Задействование GSon конвертора
                .build();
        return retrofit;
    }

    //Описание интерфейса
    public interface APIInterface {
        @GET("weather")  //Запросы к сайту openweather
        Call<WeatherReturn>
        getData(@Query("lat") Double lat,
                 @Query("lon") Double lon,
                 @Query("appid") String appid
        );
    }
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
    class WeatherReturn //POJO класс
    {
        private class WeatherTemp
        {
            Double temp;
        }
        public class WeatherDescription
        {
            String main;
        }

        public WeatherReturn(List<WeatherDescription> description)
        {
            this.descr_list = descr_list;
        }

        @SerializedName("main") //Названия соответствуют названиям в JSON
        private WeatherTemp temp; //Имя переменной произвольное
        @SerializedName("name")
        private String name;
        @SerializedName("weather")
        private List<WeatherDescription> descr_list;


        public String getTempCelsium() { return String.valueOf(temp.temp.intValue()-273) + " C"; }  //Перевод температуры из Кельвина В Цельсий и отправка
        public String getCity()
        {
            return name;
        } //Отправка названия города
        public String getDescription()
        {
            return descr_list.get(0).main;
        } //Отправка описания погоды

    }
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
    //
    //Метод инициализации запроса погоды
    public void getWeather()
    {
        //
        // Данные из примера от openweathermap.org
        Double lat = 51.51;
        Double lon = -0.13;
        String key = "7b2a6a4dd86539addd8cee86e197621f";

        //
        //Асинхронный запрос
        Call<WeatherReturn> callWeather = api.getData(lat, lon, key);
        callWeather.enqueue(new Callback<WeatherReturn>()
        {
            @Override
            public void onResponse(Call<WeatherReturn> call, Response<WeatherReturn> response)
            {
                WeatherReturn data = response.body(); //Получаем обьект типа response, распарсенный методом body()

                if (response.isSuccessful())
                {
                    //Присвоение данных переменной DATA
                    DATA.setText("\n City: " + data.getCity() + " \n\n Degrees:" + data.getTempCelsium() +" \n\n "+ data.getDescription());
                }
            }
            @Override
            public void onFailure(Call<WeatherReturn> call, Throwable t)
            {
            }
        });
    }
}