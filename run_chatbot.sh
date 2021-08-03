 
export CUR_DIR=`pwd`
cd $CUR_DIR
cd burt-server
screen_name="burt-server"
screen -dmS $screen_name
screen -S $screen_name -X screen ./update_deps_and_run_server.sh

cd $CUR_DIR
cd burt-gui
screen_name="burt-gui"
screen -dmS $screen_name
screen -S $screen_name -X screen ./run_app.sh
