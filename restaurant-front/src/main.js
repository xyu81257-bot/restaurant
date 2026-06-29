// src/main.js
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.config.productionTip = false

Vue.use(ElementUI)

// 注意：这里不要引入 interceptor
// 因为我们在 api/index.js 中已经配置了拦截器
new Vue({
  router,
  render: h => h(App)
}).$mount('#app')