export CLASSPATH=${0%/*}/classes.dex
killall -9 Grave
exec app_process /system/bin --nice-name=Grave com.moe.processmanager.Grave
