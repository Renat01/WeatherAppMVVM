package com.weather.app.mvvm.viewmodel.main;

import android.util.Log;
import android.view.View;

import com.weather.app.mvvm.data.model.WeatherBody;
import com.weather.app.mvvm.data.model.item.ListWeather;
import com.weather.app.mvvm.data.model.item.ThreeHourWeatherBody;
import com.weather.app.mvvm.data.network.ApiClient;
import com.weather.app.mvvm.data.network.ApiWeatherService;
import com.weather.app.mvvm.utils.Constant;

import java.util.List;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.weather.app.mvvm.utils.Constant.TAG;

public class MainScreenRepository {
    private static MainScreenRepository instance;
    private ApiWeatherService apiWeatherService;
    private CompositeDisposable compositeDisposable;

    private ObservableInt loading;
    private ObservableInt content;
    private ObservableInt error;
    private ObservableBoolean isLoading;

    public static MainScreenRepository getInstance() {
        if (instance == null) {
            instance = new MainScreenRepository();
        }

        return instance;
    }

    private MainScreenRepository() {
        apiWeatherService = ApiClient.getInstance().getApiService();
        compositeDisposable = new CompositeDisposable();

        loading = new ObservableInt();
        content = new ObservableInt();
        error = new ObservableInt();
        isLoading = new ObservableBoolean();
    }

    public MutableLiveData<WeatherBody> getInfoWeather(String city) {
        MutableLiveData<WeatherBody> liveData = new MutableLiveData<>();

        loading.set(View.VISIBLE);
        content.set(View.GONE);
        error.set(View.GONE);
        isLoading.set(true);

        compositeDisposable.add(apiWeatherService.getApiWeather(city, Constant.UNITS_METRIC, Constant.LANG)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherBody -> {
                    liveData.setValue(weatherBody);
                    content.set(View.VISIBLE);
                    loading.set(View.GONE);
                    isLoading.set(false);

                }, throwable -> {
                    error.set(View.VISIBLE);
                    content.set(View.GONE);
                    loading.set(View.GONE);
                    isLoading.set(false);

                    throwable.printStackTrace();
                }));

        return liveData;
    }

    public MutableLiveData<ThreeHourWeatherBody> getThreeHourWeather(String city) {
        MutableLiveData<ThreeHourWeatherBody> liveData = new MutableLiveData<>();

        compositeDisposable.add(apiWeatherService.getApiWeatherThreeHour(city, Constant.UNITS_METRIC, Constant.LANG)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(liveData::setValue, Throwable::printStackTrace));

        return liveData;
    }

    public ObservableInt getLoading() {
        return loading;
    }

    public ObservableInt getContent() {
        return content;
    }

    public ObservableBoolean isLoading() {
        return isLoading;
    }

    public ObservableInt getError() {
        return error;
    }
}
