import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

const data = [
  { month: "Jan", expense: 20000 },
  { month: "Feb", expense: 25000 },
  { month: "Mar", expense: 18000 },
  { month: "Apr", expense: 30000 },
];

export default function MonthlyBarChart() {
  return (
    <article className="dashboard-card chart-card">
      <h2>Monthly Expenses</h2>

      <div className="chart-card__body">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data}>
            <XAxis dataKey="month" stroke="#9aa8bd" />
            <YAxis stroke="#9aa8bd" />
            <Tooltip />
            <Bar dataKey="expense" fill="#60a5fa" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </article>
  );
}
