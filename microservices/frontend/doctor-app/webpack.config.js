const ModuleFederationPlugin = require('webpack').container.ModuleFederationPlugin;

/**
 * Doctor App - Micro-Frontend
 * Independent deployment unit
 */
module.exports = {
  mode: 'development',
  devServer: {
    port: 3002,
  },
  plugins: [
    new ModuleFederationPlugin({
      name: 'doctorApp',
      filename: 'remoteEntry.js',
      exposes: {
        './PatientMonitoring': './src/pages/doctor/PatientMonitoringDashboard',
        './TeleConsultation': './src/pages/TeleConsultation',
      },
      shared: {
        react: { singleton: true },
        'react-dom': { singleton: true },
        '@mui/material': { singleton: true },
      },
    }),
  ],
};






