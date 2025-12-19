const ModuleFederationPlugin = require('webpack').container.ModuleFederationPlugin;

/**
 * Admin App - Micro-Frontend
 * Independent deployment unit
 */
module.exports = {
  mode: 'development',
  devServer: {
    port: 3003,
  },
  plugins: [
    new ModuleFederationPlugin({
      name: 'adminApp',
      filename: 'remoteEntry.js',
      exposes: {
        './AdminDashboard': './src/pages/admin/AdminDashboard',
        './UserManagement': './src/pages/admin/UserManagement',
        './Reports': './src/pages/admin/Reports',
      },
      shared: {
        react: { singleton: true },
        'react-dom': { singleton: true },
        '@mui/material': { singleton: true },
      },
    }),
  ],
};

