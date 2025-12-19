const ModuleFederationPlugin = require('webpack').container.ModuleFederationPlugin;
const path = require('path');

/**
 * Webpack 5 Module Federation Configuration
 * Micro-Frontends Architecture
 */
module.exports = {
  mode: 'development',
  devServer: {
    port: 3000,
    historyApiFallback: true,
  },
  plugins: [
    new ModuleFederationPlugin({
      name: 'shell',
      remotes: {
        patientApp: 'patientApp@http://localhost:3001/remoteEntry.js',
        doctorApp: 'doctorApp@http://localhost:3002/remoteEntry.js',
        adminApp: 'adminApp@http://localhost:3003/remoteEntry.js',
      },
      shared: {
        react: { singleton: true, requiredVersion: '^18.2.0' },
        'react-dom': { singleton: true, requiredVersion: '^18.2.0' },
        '@mui/material': { singleton: true },
        '@mui/icons-material': { singleton: true },
      },
    }),
  ],
  resolve: {
    extensions: ['.js', '.jsx', '.ts', '.tsx'],
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx|ts|tsx)$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-react', '@babel/preset-typescript'],
          },
        },
      },
    ],
  },
};

