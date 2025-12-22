import React, { Suspense } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Box, CircularProgress } from '@mui/material';

/**
 * App Shell - Micro-Frontends Container
 * Loads remote modules dynamically
 */
const PatientDashboard = React.lazy(() => import('patientApp/PatientDashboard'));
const JourneyTimeline = React.lazy(() => import('patientApp/JourneyTimeline'));
const MedicalPassport = React.lazy(() => import('patientApp/MedicalPassport'));
const EmergencyHelp = React.lazy(() => import('patientApp/EmergencyHelp'));
const CaseManagement = React.lazy(() => import('patientApp/CaseManagement'));

const PatientMonitoring = React.lazy(() => import('doctorApp/PatientMonitoring'));
const TeleConsultation = React.lazy(() => import('doctorApp/TeleConsultation'));

const AdminDashboard = React.lazy(() => import('adminApp/AdminDashboard'));
const UserManagement = React.lazy(() => import('adminApp/UserManagement'));
const Reports = React.lazy(() => import('adminApp/Reports'));

const AppShell = () => {
  return (
    <Router>
      <Suspense fallback={
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
          <CircularProgress />
        </Box>
      }>
        <Routes>
          {/* Patient Routes */}
          <Route path="/dashboard" element={<PatientDashboard />} />
          <Route path="/journey-timeline" element={<JourneyTimeline />} />
          <Route path="/medical-passport" element={<MedicalPassport />} />
          <Route path="/emergency-help" element={<EmergencyHelp />} />
          <Route path="/case-management" element={<CaseManagement />} />
          
          {/* Doctor Routes */}
          <Route path="/doctor/patients" element={<PatientMonitoring />} />
          <Route path="/tele-consultation/:patientId?" element={<TeleConsultation />} />
          
          {/* Admin Routes */}
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
          <Route path="/admin/users" element={<UserManagement />} />
          <Route path="/admin/reports" element={<Reports />} />
        </Routes>
      </Suspense>
    </Router>
  );
};

export default AppShell;






