call yarn install
call cd ../react-chatbot-kit
call yarn install
call cp build/index.js ../burt-gui/node_modules/react-chatbot-kit/build/index.js
call cd ../burt-gui
call yarn start
