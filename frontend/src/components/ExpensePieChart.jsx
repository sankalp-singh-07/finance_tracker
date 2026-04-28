import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

const data = [
  { name: "Food", value: 40 },
  { name: "Rent", value: 30 },
  { name: "Travel", value: 15 },
  { name: "Others", value: 15 },
];

const COLORS = ["#4ade80", "#60a5fa", "#f97316", "#a78bfa"];

export default function ExpensePieChart() {
  return (
    <article className="dashboard-card chart-card">
      <h2>Expense Breakdown</h2>

      <div className="chart-card__body">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie data={data} dataKey="value" outerRadius={90}>
              {data.map((_, index) => (
                <Cell key={index} fill={COLORS[index]} />
              ))}
            </Pie>
            <Tooltip />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </article>
  );
}
