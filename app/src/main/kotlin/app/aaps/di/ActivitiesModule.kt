package app.aaps.di

import app.aaps.MainActivity
import app.aaps.activities.HistoryBrowseActivity
import app.aaps.activities.MyPreferenceFragment
import app.aaps.activities.PreferencesActivity
import app.aaps.plugins.source.activities.EversenseCalibrationActivity
import app.aaps.plugins.source.activities.EversensePlacementActivity
import app.aaps.plugins.source.activities.RequestEversensePermissionActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("unused")
abstract class ActivitiesModule {

    @ContributesAndroidInjector abstract fun contributesHistoryBrowseActivity(): HistoryBrowseActivity
    @ContributesAndroidInjector abstract fun contributesMainActivity(): MainActivity
    @ContributesAndroidInjector abstract fun contributesPreferencesActivity(): PreferencesActivity
    @ContributesAndroidInjector abstract fun contributesPreferencesFragment(): MyPreferenceFragment
    @ContributesAndroidInjector abstract fun contributesRequestEversensePermissionActivity(): RequestEversensePermissionActivity
    @ContributesAndroidInjector abstract fun contributesEversenseCalibrationActivity(): EversenseCalibrationActivity
    @ContributesAndroidInjector abstract fun contributesEversensePlacementActivity(): EversensePlacementActivity
}
