import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Button, Card, CardContent, Grid, Typography, Container, CircularProgress } from '@mui/material';
import apiClient from '../../api/apiClient';

const StudentDashboard = () => {
  const [upcomingExams, setUpcomingExams] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchUpcomingExams();
  }, []);

  const fetchUpcomingExams = async () => {
    try {
      const response = await apiClient.get('/student/exams/upcoming');
      setUpcomingExams(response.data);
    } catch (error) {
      console.error('Error fetching exams:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleStartExam = (examId) => {
    navigate(`/student/exam/${examId}`);
  };

  if (loading) {
    return <CircularProgress />;
  }

  return (
    <Container maxWidth="lg">
      <Box sx={{ py: 4 }}>
        <Typography variant="h4" sx={{ mb: 4 }}>
          Student Dashboard
        </Typography>

        <Grid container spacing={2} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Button fullWidth variant="contained" onClick={() => navigate('/student/portfolio')}>
              Portfolio
            </Button>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Button fullWidth variant="contained" onClick={() => navigate('/student/leaderboard')}>
              Leaderboard
            </Button>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Button fullWidth variant="contained" onClick={() => navigate('/student/cgpa-predictor')}>
              CGPA Predictor
            </Button>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Button fullWidth variant="outlined">
              Settings
            </Button>
          </Grid>
        </Grid>

        <Typography variant="h5" sx={{ mb: 2 }}>
          Upcoming Exams
        </Typography>

        <Grid container spacing={2}>
          {upcomingExams.map((exam) => (
            <Grid item xs={12} md={6} key={exam.examId}>
              <Card>
                <CardContent>
                  <Typography variant="h6">{exam.examName}</Typography>
                  <Typography color="textSecondary">{exam.subject}</Typography>
                  <Typography variant="body2">Duration: {exam.durationMinutes} minutes</Typography>
                  <Typography variant="body2">Total Marks: {exam.totalMarks}</Typography>
                  <Button
                    variant="contained"
                    sx={{ mt: 2 }}
                    onClick={() => handleStartExam(exam.examId)}
                  >
                    Start Exam
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>
    </Container>
  );
};

export default StudentDashboard;
