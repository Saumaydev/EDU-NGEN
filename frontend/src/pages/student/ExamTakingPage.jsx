import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Button, Card, CardContent, Typography, Container, TextField, Alert, CircularProgress, LinearProgress } from '@mui/material';
import apiClient from '../../api/apiClient';

const ExamTakingPage = () => {
  const { examId } = useParams();
  const navigate = useNavigate();
  const [exam, setExam] = useState(null);
  const [attemptId, setAttemptId] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState({});
  const [timeLeft, setTimeLeft] = useState(0);
  const [loading, setLoading] = useState(true);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    startExam();
  }, []);

  useEffect(() => {
    if (timeLeft > 0 && !submitted) {
      const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
      return () => clearTimeout(timer);
    } else if (timeLeft === 0 && attemptId) {
      submitExam();
    }
  }, [timeLeft, submitted]);

  const startExam = async () => {
    try {
      const examResponse = await apiClient.get(`/student/exams/${examId}/details`);
      setExam(examResponse.data);

      const attemptResponse = await apiClient.post(`/student/exams/${examId}/start`);
      const newAttemptId = attemptResponse.data.attemptId;
      setAttemptId(newAttemptId);
      setTimeLeft(attemptResponse.data.duration * 60);

      const questionsResponse = await apiClient.get(`/student/exams/${examId}/attempt/${newAttemptId}/questions`);
      setQuestions(questionsResponse.data);
    } catch (error) {
      console.error('Error starting exam:', error);
    } finally {
      setLoading(false);
    }
  };

  const saveResponse = async () => {
    if (!questions[currentQuestion] || !attemptId) return;

    const question = questions[currentQuestion];
    const response = {
      questionId: question.questionId,
      selectedOptionId: answers[question.questionId]?.optionId,
      answerText: answers[question.questionId]?.text,
    };

    try {
      await apiClient.post(`/student/exams/${examId}/attempt/${attemptId}/save-response`, response);
    } catch (error) {
      console.error('Error saving response:', error);
    }
  };

  const submitExam = async () => {
    if (submitted) return;
    setSubmitted(true);

    try {
      const response = await apiClient.post(`/student/exams/${examId}/attempt/${attemptId}/submit`);
      setTimeout(() => {
        navigate(`/student/results/${attemptId}`);
      }, 2000);
    } catch (error) {
      console.error('Error submitting exam:', error);
    }
  };

  const handleAnswerChange = (value) => {
    const question = questions[currentQuestion];
    setAnswers({
      ...answers,
      [question.questionId]: value,
    });
  };

  if (loading) return <CircularProgress />;
  if (submitted) return <Typography>Exam submitted! Redirecting...</Typography>;
  if (!exam || questions.length === 0) return <Typography>No exam data</Typography>;

  const question = questions[currentQuestion];
  const progress = ((currentQuestion + 1) / questions.length) * 100;
  const minutes = Math.floor(timeLeft / 60);
  const seconds = timeLeft % 60;

  return (
    <Container maxWidth="lg">
      <Box sx={{ py: 2 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
          <Typography variant="h5">{exam.examName}</Typography>
          <Typography variant="h6" sx={{ color: timeLeft < 300 ? 'error.main' : 'inherit' }}>
            Time: {minutes}:{seconds.toString().padStart(2, '0')}
          </Typography>
        </Box>

        <LinearProgress variant="determinate" value={progress} sx={{ mb: 2 }} />

        <Card sx={{ mb: 2 }}>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Question {currentQuestion + 1} of {questions.length}
            </Typography>
            <Typography sx={{ mb: 2 }}>{question.questionText}</Typography>

            {question.questionType === 'MCQ' && question.options && (
              <Box>
                {question.options.map((option) => (
                  <Button
                    key={option.optionId}
                    fullWidth
                    variant={answers[question.questionId]?.optionId === option.optionId ? 'contained' : 'outlined'}
                    sx={{ mb: 1, justifyContent: 'flex-start' }}
                    onClick={() => handleAnswerChange({ optionId: option.optionId })}
                  >
                    {option.optionText}
                  </Button>
                ))}
              </Box>
            )}

            {question.questionType === 'SUBJECTIVE' && (
              <TextField
                fullWidth
                multiline
                rows={4}
                placeholder="Enter your answer"
                value={answers[question.questionId]?.text || ''}
                onChange={(e) => handleAnswerChange({ text: e.target.value })}
              />
            )}
          </CardContent>
        </Card>

        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="outlined"
            disabled={currentQuestion === 0}
            onClick={() => {
              saveResponse();
              setCurrentQuestion(currentQuestion - 1);
            }}
          >
            Previous
          </Button>

          <Button
            variant="contained"
            onClick={saveResponse}
          >
            Save
          </Button>

          {currentQuestion < questions.length - 1 && (
            <Button
              variant="outlined"
              onClick={() => {
                saveResponse();
                setCurrentQuestion(currentQuestion + 1);
              }}
            >
              Next
            </Button>
          )}

          {currentQuestion === questions.length - 1 && (
            <Button
              variant="contained"
              color="success"
              onClick={submitExam}
            >
              Submit Exam
            </Button>
          )}
        </Box>
      </Box>
    </Container>
  );
};

export default ExamTakingPage;
