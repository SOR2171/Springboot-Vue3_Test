import {createApp} from 'vue'
import App from './App.vue'
import router from "./router/index.js";
import axios from "axios";

axios.defaults.baseURL ='http://172.17.180.132:8080'

const app = createApp(App)

app.use(router)

app.mount('#app')
