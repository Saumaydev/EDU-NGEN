import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Provider } from 'react-redux';
import store from './store';
import PrivateRoute from './components/PrivateRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import StudentDashboard from './pages/student/StudentDashboard';
import TeacherDashboard from './pages/teacher/TeacherDashboard';
import ExamTakingPage from './pages/student/ExamTakingPage';
import ExamResultsPage from './pages/student/ExamResultsPage';
import ExamBuilderPage from './pages/teacher/ExamBuilderPage';
import StudentPortfolioPage from './pages/student/StudentPortfolioPage';
import LeaderboardPage from './pages/student/LeaderboardPage';
import CGPAPredictorPage from './pages/student/CGPAPredictorPage';

function App() {
  return (
    <Provider store={store}>
      <Router>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/student/dashboard" element={<PrivateRoute role="STUDENT"><StudentDashboard /></PrivateRoute>} />
          <Route path="/student/portfolio" element={<PrivateRoute role="STUDENT"><StudentPortfolioPage /></PrivateRoute>} />
          <Route path="/student/leaderboard" element={<PrivateRoute role="STUDENT"><LeaderboardPage /></PrivateRoute>} />
          <Route path="/student/cgpa-predictor" element={<PrivateRoute role="STUDENT"><CGPAPredictorPage /></PrivateRoute>} />
          <Route path="/student/exam/:examId" element={<PrivateRoute role="STUDENT"><ExamTakingPage /></PrivateRoute>} />
          <Route path="/student/results/:attemptId" element={<PrivateRoute role="STUDENT"><ExamResultsPage /></PrivateRoute>} />
          <Route path="/teacher/dashboard" element={<PrivateRoute role="TEACHER"><TeacherDashboard /></PrivateRoute>} />
          <Route path="/teacher/exam/new" element={<PrivateRoute role="TEACHER"><ExamBuilderPage /></PrivateRoute>} />
          <Route path="/teacher/exam/:examId/edit" element={<PrivateRoute role="TEACHER"><ExamBuilderPage /></PrivateRoute>} />
          <Route path="/" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </Provider>
  );
}

export default App;
