import { createSlice } from '@reduxjs/toolkit';

const examSlice = createSlice({
  name: 'exam',
  initialState: {
    exams: [],
    currentExam: null,
    upcomingExams: [],
    attempts: [],
    currentAttempt: null,
    loading: false,
    error: null,
  },
  reducers: {
    setExams: (state, action) => {
      state.exams = action.payload;
    },
    setCurrentExam: (state, action) => {
      state.currentExam = action.payload;
    },
    setUpcomingExams: (state, action) => {
      state.upcomingExams = action.payload;
    },
    setCurrentAttempt: (state, action) => {
      state.currentAttempt = action.payload;
    },
    setAttempts: (state, action) => {
      state.attempts = action.payload;
    },
    setLoading: (state, action) => {
      state.loading = action.payload;
    },
    setError: (state, action) => {
      state.error = action.payload;
    },
  },
});

export const { setExams, setCurrentExam, setUpcomingExams, setCurrentAttempt, setAttempts, setLoading, setError } = examSlice.actions;
export default examSlice.reducer;
