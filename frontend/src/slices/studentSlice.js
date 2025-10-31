import { createSlice } from '@reduxjs/toolkit';

const studentSlice = createSlice({
  name: 'student',
  initialState: {
    portfolio: null,
    performanceTrends: null,
    subjectWisePerformance: null,
    leaderboard: [],
    loading: false,
    error: null,
  },
  reducers: {
    setPortfolio: (state, action) => {
      state.portfolio = action.payload;
    },
    setPerformanceTrends: (state, action) => {
      state.performanceTrends = action.payload;
    },
    setSubjectWisePerformance: (state, action) => {
      state.subjectWisePerformance = action.payload;
    },
    setLeaderboard: (state, action) => {
      state.leaderboard = action.payload;
    },
    setLoading: (state, action) => {
      state.loading = action.payload;
    },
    setError: (state, action) => {
      state.error = action.payload;
    },
  },
});

export const { setPortfolio, setPerformanceTrends, setSubjectWisePerformance, setLeaderboard, setLoading, setError } = studentSlice.actions;
export default studentSlice.reducer;
