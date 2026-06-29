module.exports = {
  lintOnSave: false,

  devServer: {
    host: '0.0.0.0',
    port: 8081,  // 前端开发服务器端口
    allowedHosts: 'all',

    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端实际端口（根据您的后端修改）
        changeOrigin: true
      }
      // 其他 /dish、/category 等代理可以删除，因为现在统一用 /api
    },

    onListening(server) {
      const port = server.options.port
      console.log('\n========================================')
      console.log('🍽 餐厅管理后台入口: http://localhost:' + port + '/admin/dashboard')
      console.log('========================================\n')
    }
  }
}