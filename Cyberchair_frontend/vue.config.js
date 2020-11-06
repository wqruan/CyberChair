module.exports = {
  devServer: {
    host: "0.0.0.0",
      port: 9999,
      proxy: {
          '/api': {
              target: 'http://localhost:8080/',
              changeOrigin: true,
              ws: true,
              pathRewrite: {
                '^/api': ''
              }
          },

          '/bm': {
            target: 'http://localhost:8080/',
            changeOrigin: true,
            ws: true,
            pathRewrite: {
              '^/api': ''
            }
        },

      },
      disableHostCheck: true,
  },
  transpileDependencies: ['vuetify'],

  pluginOptions: {
    i18n: {
      locale: 'en',
      fallbackLocale: 'en',
      localeDir: 'locales',
      enableInSFC: false,
    },
  },
}
