const ModuleFederationPlugin = require('webpack').container.ModuleFederationPlugin;

/**
 * Patient App - Micro-Frontend
 * Independent deployment unit
 */
module.exports = {
  mode: 'development',
  devServer: {
    port: 3001,
  },
  plugins: [
    new ModuleFederationPlugin({
      name: 'patientApp',
      filename: 'remoteEntry.js',
      exposes: {
        './PatientDashboard': './src/pages/Dashboard',
        './JourneyTimeline': './src/pages/JourneyTimeline',
        './MedicalPassport': './src/pages/MedicalPassport',
        './EmergencyHelp': './src/pages/EmergencyHelp',
        './CaseManagement': './src/pages/CaseManagement',
      },
      shared: {
        react: { singleton: true },
        'react-dom': { singleton: true },
        '@mui/material': { singleton: true },
      },
    }),
  ],
};






