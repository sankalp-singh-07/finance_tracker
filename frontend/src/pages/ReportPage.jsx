import { useMemo, useState } from "react";
import "./Dashboard.css";
import "./ReportPage.css";

function getCurrentMonth() {
  return new Date().toISOString().slice(0, 7);
}

function formatCurrency(value) {
  return new Intl.NumberFormat("en-IN", {
    currency: "INR",
    maximumFractionDigits: 0,
    style: "currency",
  }).format(Number(value || 0));
}


const MONTHLY_DATA = {
  "2025-01": { income: 85000, expenses: { Food: 9200, Rent: 22000, Utilities: 3100, Transport: 4800, Health: 1500 } },
  "2025-02": { income: 85000, expenses: { Food: 10400, Rent: 22000, Utilities: 2800, Transport: 5200, Health: 800 } },
  "2025-03": { income: 92000, expenses: { Food: 11000, Rent: 22000, Utilities: 3400, Transport: 4600, Health: 2200 } },
  "2025-04": { income: 85000, expenses: { Food: 9800, Rent: 22000, Utilities: 2600, Transport: 5000, Health: 600 } },
  "2025-05": { income: 85000, expenses: { Food: 10200, Rent: 22000, Utilities: 3000, Transport: 4400, Health: 1800 } },
  "2025-06": { income: 95000, expenses: { Food: 12500, Rent: 22000, Utilities: 3200, Transport: 5800, Health: 3100 } },
  "2025-07": { income: 85000, expenses: { Food: 9500, Rent: 22000, Utilities: 2900, Transport: 4700, Health: 900 } },
  "2025-08": { income: 85000, expenses: { Food: 10800, Rent: 22000, Utilities: 3100, Transport: 5100, Health: 1200 } },
  "2025-09": { income: 90000, expenses: { Food: 11200, Rent: 22000, Utilities: 3300, Transport: 5500, Health: 700 } },
  "2025-10": { income: 85000, expenses: { Food: 9600, Rent: 22000, Utilities: 2700, Transport: 4900, Health: 2400 } },
  "2025-11": { income: 85000, expenses: { Food: 10500, Rent: 22000, Utilities: 3500, Transport: 5300, Health: 1600 } },
  "2025-12": { income: 98000, expenses: { Food: 13200, Rent: 22000, Utilities: 4100, Transport: 6200, Health: 2800 } },
};

const CATEGORY_COLORS = {
  Food: "#267366",
  Rent: "#3b6d11",
  Utilities: "#ba7517",
  Transport: "#185fa5",
  Health: "#993556",
};

function getDataForMonth(month) {
  return (
    MONTHLY_DATA[month] || {
      income: 85000,
      expenses: { Food: 10000, Rent: 22000, Utilities: 3000, Transport: 5000, Health: 1500 },
    }
  );
}

function getLast6Months(currentMonth) {
  const [year, month] = currentMonth.split("-").map(Number);
  const months = [];
  for (let i = 5; i >= 0; i--) {
    let m = month - i;
    let y = year;
    while (m <= 0) { m += 12; y -= 1; }
    const key = `${y}-${String(m).padStart(2, "0")}`;
    const label = new Date(y, m - 1).toLocaleString("en-IN", { month: "short" });
    const data = getDataForMonth(key);
    const totalExpenses = Object.values(data.expenses).reduce((a, b) => a + b, 0);
    months.push({ key, label, income: data.income, expenses: totalExpenses });
  }
  return months;
}

function generateInsights(month, income, expenseTotal, expenses) {
  const savings = income - expenseTotal;
  const savingsRate = income > 0 ? Math.round((savings / income) * 100) : 0;
  const sortedCategories = Object.entries(expenses).sort((a, b) => b[1] - a[1]);
  const [topCat, topAmt] = sortedCategories[0] || ["Food", 0];
  const topPct = expenseTotal > 0 ? Math.round((topAmt / expenseTotal) * 100) : 0;

  const insights = [
    {
      icon: "↑",
      color: "teal",
      text: `Your savings rate this month is ${savingsRate}%. ${savingsRate >= 20 ? "Great job — you're above the recommended 20% target." : "Try to aim for at least 20% savings each month."}`,
    },
    {
      icon: "•",
      color: "coral",
      text: `${topCat} is your largest expense category at ${topPct}% of total spending (${formatCurrency(topAmt)}).`,
    },
    {
      icon: "•",
      color: "blue",
      text: `Total expenses of ${formatCurrency(expenseTotal)} are ${expenseTotal < income ? "within" : "over"} your income of ${formatCurrency(income)}.`,
    },
  ];

  const rentAmt = expenses["Rent"] || 0;
  if (rentAmt > 0) {
    const rentPct = Math.round((rentAmt / income) * 100);
    insights.push({
      icon: "→",
      color: "amber",
      text: `Rent takes up ${rentPct}% of your income. Financial experts recommend keeping housing costs below 30%.`,
    });
  }

  return insights;
}



function BarChart({ months }) {
  const maxVal = Math.max(...months.flatMap((m) => [m.income, m.expenses]));

  return (
    <div className="report-bar-chart">
      <div className="report-bar-chart__legend">
        <span className="report-bar-chart__legend-income">Income</span>
        <span className="report-bar-chart__legend-expense">Expenses</span>
      </div>
      <div className="report-bar-chart__bars">
        {months.map((m) => (
          <div className="report-bar-chart__group" key={m.key}>
            <div className="report-bar-chart__pair">
              <div
                className="report-bar report-bar--income"
                style={{ height: `${Math.round((m.income / maxVal) * 100)}%` }}
                title={formatCurrency(m.income)}
              />
              <div
                className="report-bar report-bar--expense"
                style={{ height: `${Math.round((m.expenses / maxVal) * 100)}%` }}
                title={formatCurrency(m.expenses)}
              />
            </div>
            <span className="report-bar-chart__label">{m.label}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

function CategoryTable({ expenses, total }) {
  const rows = Object.entries(expenses)
    .map(([name, amount]) => ({ name, amount, pct: total > 0 ? Math.round((amount / total) * 100) : 0 }))
    .sort((a, b) => b.amount - a.amount);

  return (
    <table className="report-table">
      <thead>
        <tr>
          <th>Category</th>
          <th>Amount</th>
          <th>Share</th>
        </tr>
      </thead>
      <tbody>
        {rows.map((row) => (
          <tr key={row.name}>
            <td>
              <span
                className="report-table__dot"
                style={{ background: CATEGORY_COLORS[row.name] || "#888" }}
              />
              {row.name}
            </td>
            <td>{formatCurrency(row.amount)}</td>
            <td>
              <div className="report-table__bar-wrap">
                <div
                  className="report-table__bar"
                  style={{ width: `${row.pct}%`, background: CATEGORY_COLORS[row.name] || "#267366" }}
                />
                <span>{row.pct}%</span>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}


export default function ReportPage({ onLogout, onNavigate, user }) {
  const [month, setMonth] = useState(getCurrentMonth);

  const { income, expenses } = useMemo(() => getDataForMonth(month), [month]);
  const expenseTotal = useMemo(
    () => Object.values(expenses).reduce((a, b) => a + b, 0),
    [expenses]
  );
  const savings = income - expenseTotal;
  const last6Months = useMemo(() => getLast6Months(month), [month]);
  const insights = useMemo(
    () => generateInsights(month, income, expenseTotal, expenses),
    [month, income, expenseTotal, expenses]
  );

  return (
    <main className="dashboard report-page">
      <div className="dashboard__shell">
        {/* Nav */}
        <nav className="dashboard__nav">
          <button
            className="dashboard__brand dashboard__brand-button"
            onClick={() => onNavigate("dashboard")}
            type="button"
          >
            <span className="dashboard__brand-mark">F</span>
            <span>FinSight</span>
          </button>

          <div className="dashboard__nav-links" aria-label="Dashboard navigation">
            <button onClick={() => onNavigate("dashboard")} type="button">
              Overview
            </button>
            <button onClick={() => onNavigate("budgets")} type="button">
              Budgets
            </button>
            <button className="is-active" type="button">
              Reports
            </button>
          </div>

          <label className="budget-month">
            <span>Month</span>
            <input
              onChange={(e) => setMonth(e.target.value)}
              type="month"
              value={month}
            />
          </label>

          <button className="dashboard__logout" onClick={onLogout} type="button">
            Logout
          </button>
        </nav>

        {/* Header */}
        <header className="dashboard__header report-hero">
          <div>
            <p className="dashboard__eyebrow">Reports &amp; analytics</p>
            <h1>Financial Reports</h1>
            <p className="dashboard__intro">
              Visualise income versus expenses, track category spend, and get
              personalised insights to improve your financial health.
            </p>
          </div>
          <div className="dashboard__profile">
            <span>Signed in as</span>
            <strong>{user?.name || "User"}</strong>
            <small>Demo mode</small>
          </div>
        </header>

        {/* Demo banner */}
        <p className="report-demo-banner">
          Demo mode — showing sample data for an Indian household. Connect a backend to see real figures.
        </p>

        {/* Summary strip */}
        <section className="report-summary" aria-label="Report summary">
          <article>
            <span>Total income</span>
            <strong className="report-summary__income">{formatCurrency(income)}</strong>
          </article>
          <article>
            <span>Total expenses</span>
            <strong className="report-summary__expense">{formatCurrency(expenseTotal)}</strong>
          </article>
          <article>
            <span>Net savings</span>
            <strong className={savings >= 0 ? "report-summary__savings" : "report-summary__deficit"}>
              {formatCurrency(savings)}
            </strong>
          </article>
        </section>

        {/* Charts grid */}
        <section className="report-grid" aria-label="Report charts">
          <div className="report-card">
            <p className="report-card__label">Income vs expenses</p>
            <h2 className="report-card__title">Last 6 months</h2>
            <BarChart months={last6Months} />
          </div>

          <div className="report-card">
            <p className="report-card__label">Category breakdown</p>
            <h2 className="report-card__title">
              {new Date(month + "-01").toLocaleString("en-IN", { month: "long", year: "numeric" })}
            </h2>
            <CategoryTable expenses={expenses} total={expenseTotal} />
          </div>
        </section>

        {/* Insights */}
        <section className="report-insights" aria-label="Insights">
          <p className="report-card__label">Auto insights</p>
          <h2 className="report-card__title">What your data says</h2>
          <div className="report-insights__grid">
            {insights.map((ins, i) => (
              <article className={`report-insight report-insight--${ins.color}`} key={i}>
                <span className="report-insight__icon">{ins.icon}</span>
                <p>{ins.text}</p>
              </article>
            ))}
          </div>
        </section>
      </div>
    </main>
  );
}