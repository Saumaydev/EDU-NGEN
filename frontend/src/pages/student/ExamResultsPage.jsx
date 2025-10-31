import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Card, CardContent, Typography, Container, Button, CircularProgress, LinearProgress, Grid } from '@mui/material';
import apiClient from '../../api/apiClient';

const ExamResultsPage = () => {
  const { attemptId } = useParams();
  const navigate = useNavigate();
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchResults();
  }, []);

  const fetchResults = async () => {
    try {
      // Note: This would need proper exam and attempt IDs from routing
      // Using a simulated endpoint for now
      const response = await apiClient.get(`/student/exams/1/attempt/${attemptId}/results`);
      setResults(response.data);
    } catch (error) {
      console.error('Error fetching results:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <CircularProgress />;
  if (!results) return <Typography>Results not available</Typography>;

  const percentage = results.totalMarksPercentage || 0;
  const isPassed = percentage >= 50;

  return (
    <Container maxWidth="md">
      <Box sx={{ py: 4 }}>
        <Typography variant="h4" sx={{ mb: 2, textAlign: 'center' }}>
          Exam Results
        </Typography>

        <Card sx={{ mb: 4 }}>
          <CardContent>
            <Typography variant="h5" sx={{ mb: 2, textAlign: 'center', color: isPassed ? 'success.main' : 'error.main' }}>
              {isPassed ? '✓ PASSED' : '✗ FAILED'}
            </Typography>

            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Typography color="textSecondary">Score</Typography>
                <Typography variant="h6">
                  {results.totalMarksObtained} / {results.totalMarksPossible}
                </Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography color="textSecondary">Percentage</Typography>
                <Typography variant="h6">{percentage.toFixed(2)}%</Typography>
              </Grid>
              <Grid item xs={12}>
                <LinearProgress variant="determinate" value={percentage} />
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography color="textSecondary">Rank in Class</Typography>
                <Typography variant="h6">#{results.rankInClass || 'N/A'}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography color="textSecondary">Time Spent</Typography>
                <Typography variant="h6">{results.timeSpent} seconds</Typography>
              </Grid>
            </Grid>
          </CardContent>
        </Card>

        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
          <Button variant="contained" onClick={() => navigate('/student/dashboard')}>
            Back to Dashboard
          </Button>
          <Button variant="outlined" onClick={fetchResults}>
            View Detailed Review
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default ExamResultsPage;
