import { useMemo, useState } from "react";
import Dashboard from "./pages/Dashboard";
import "./App.css";

const USERS_KEY = "finance-tracker-users";

function getStoredUsers() {
  try {
    return JSON.parse(localStorage.getItem(USERS_KEY)) || [];
  } catch {
    return [];
  }
}

function getPasswordScore(password) {
  let score = 0;

  if (password.length >= 8) score += 1;
  if (/[A-Z]/.test(password)) score += 1;
  if (/[0-9]/.test(password)) score += 1;
  if (/[^A-Za-z0-9]/.test(password)) score += 1;

  return score;
}

function AuthInput({
  autoFocus,
  error,
  label,
  name,
  onChange,
  placeholder,
  type = "text",
  value,
  children,
}) {
  return (
    <label className="auth-field">
      <span>{label}</span>
      <div className={`auth-input-wrap ${error ? "auth-input-wrap--error" : ""}`}>
        <input
          autoFocus={autoFocus}
          name={name}
          onChange={onChange}
          placeholder={placeholder}
          type={type}
          value={value}
        />
        {children}
      </div>
      {error && <small>{error}</small>}
    </label>
  );
}

function LoginPage({ onLogin, onSwitch }) {
  const [form, setForm] = useState({ email: "", password: "", remember: true });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [demoLoading, setDemoLoading] = useState(false);

  const isValid = /^\S+@\S+\.\S+$/.test(form.email) && form.password.length >= 6;

  function updateForm(event) {
    const { checked, name, type, value } = event.target;
    setError("");
    setForm((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
    }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!isValid || loading || demoLoading) return;

    setLoading(true);

    window.setTimeout(() => {
      const user = getStoredUsers().find(
        (storedUser) =>
          storedUser.email.toLowerCase() === form.email.toLowerCase() &&
          storedUser.password === form.password
      );

      if (!user) {
        setError("Invalid email or password");
        setLoading(false);
        return;
      }

      onLogin({ email: user.email, name: user.name || "User" });
      setLoading(false);
    }, 650);
  }

  function handleDemoLogin() {
    if (loading || demoLoading) return;

    setError("");
    setDemoLoading(true);

    window.setTimeout(() => {
      onLogin({
        email: "demo@finsight.app",
        name: "Demo User",
      });
      setDemoLoading(false);
    }, 500);
  }

  return (
    <section className="auth-card" aria-label="Login form">
      <p className="auth-eyebrow">Welcome back</p>
      <h1>Login to FinSight</h1>
      <p className="auth-copy">Open your dashboard and continue tracking your money.</p>

      <form onSubmit={handleSubmit} className="auth-form">
        <AuthInput
          autoFocus
          error={error}
          label="Email"
          name="email"
          onChange={updateForm}
          placeholder="you@example.com"
          type="email"
          value={form.email}
        />

        <AuthInput
          label="Password"
          name="password"
          onChange={updateForm}
          placeholder="Enter password"
          type={showPassword ? "text" : "password"}
          value={form.password}
        >
          <button
            className="auth-eye"
            onClick={() => setShowPassword((visible) => !visible)}
            type="button"
            aria-label={showPassword ? "Hide password" : "Show password"}
          >
            {showPassword ? "Hide" : "Show"}
          </button>
        </AuthInput>

        <div className="auth-row">
          <label className="auth-check">
            <input
              checked={form.remember}
              name="remember"
              onChange={updateForm}
              type="checkbox"
            />
            <span>Remember me</span>
          </label>

          <button className="auth-link" type="button">
            Forgot password?
          </button>
        </div>

        <button
          className="auth-submit"
          disabled={!isValid || loading || demoLoading}
          type="submit"
        >
          {loading && <span className="auth-spinner" aria-hidden="true" />}
          {loading ? "Signing in..." : "Login"}
        </button>

        <button
          className="auth-demo"
          disabled={loading || demoLoading}
          onClick={handleDemoLogin}
          type="button"
        >
          {demoLoading && <span className="auth-spinner auth-spinner--dark" aria-hidden="true" />}
          {demoLoading ? "Opening demo..." : "Login as demo user"}
        </button>
      </form>

      <p className="auth-switch">
        Don&apos;t have an account?{" "}
        <button onClick={onSwitch} type="button">
          Sign up
        </button>
      </p>
    </section>
  );
}

function SignupPage({ onSignup, onSwitch }) {
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const passwordScore = useMemo(
    () => getPasswordScore(form.password),
    [form.password]
  );

  const strength = ["", "Weak", "Fair", "Good", "Strong"][passwordScore];
  const isValid =
    form.name.trim().length >= 2 &&
    /^\S+@\S+\.\S+$/.test(form.email) &&
    form.password.length >= 8;

  function updateForm(event) {
    const { name, value } = event.target;
    setErrors((current) => ({ ...current, [name]: "" }));
    setForm((current) => ({ ...current, [name]: value }));
  }

  function validate() {
    const nextErrors = {};

    if (form.name.trim().length < 2) {
      nextErrors.name = "Name is too short";
    }

    if (!/^\S+@\S+\.\S+$/.test(form.email)) {
      nextErrors.email = "Enter a valid email";
    }

    if (form.password.length < 8) {
      nextErrors.password = "Password too short";
    }

    if (
      getStoredUsers().some(
        (user) => user.email.toLowerCase() === form.email.toLowerCase()
      )
    ) {
      nextErrors.email = "Email already exists";
    }

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (loading || !validate()) return;

    setLoading(true);

    window.setTimeout(() => {
      const newUser = {
        email: form.email.trim(),
        name: form.name.trim(),
        password: form.password,
      };

      const users = [...getStoredUsers(), newUser];
      localStorage.setItem(USERS_KEY, JSON.stringify(users));
      onSignup({ email: newUser.email, name: newUser.name });
      setLoading(false);
    }, 700);
  }

  return (
    <section className="auth-card" aria-label="Signup form">
      <p className="auth-eyebrow">Start free</p>
      <h1>Create your account</h1>
      <p className="auth-copy">Set up your dashboard in less than a minute.</p>

      <form onSubmit={handleSubmit} className="auth-form">
        <AuthInput
          autoFocus
          error={errors.name}
          label="Full name"
          name="name"
          onChange={updateForm}
          placeholder="Sankalp Singh"
          value={form.name}
        />

        <AuthInput
          error={errors.email}
          label="Email"
          name="email"
          onChange={updateForm}
          placeholder="you@example.com"
          type="email"
          value={form.email}
        />

        <AuthInput
          error={errors.password}
          label="Password"
          name="password"
          onChange={updateForm}
          placeholder="Create password"
          type={showPassword ? "text" : "password"}
          value={form.password}
        >
          <button
            className="auth-eye"
            onClick={() => setShowPassword((visible) => !visible)}
            type="button"
            aria-label={showPassword ? "Hide password" : "Show password"}
          >
            {showPassword ? "Hide" : "Show"}
          </button>
        </AuthInput>

        <div className="password-meter" data-score={passwordScore}>
          <div className="password-meter__track">
            <span />
            <span />
            <span />
            <span />
          </div>
          <p>
            Password strength
            {strength ? <strong>{strength}</strong> : <strong>Required</strong>}
          </p>
        </div>

        <button className="auth-submit" disabled={!isValid || loading} type="submit">
          {loading && <span className="auth-spinner" aria-hidden="true" />}
          {loading ? "Creating account..." : "Sign up"}
        </button>
      </form>

      <p className="auth-switch">
        Already have an account?{" "}
        <button onClick={onSwitch} type="button">
          Login
        </button>
      </p>
    </section>
  );
}

export default function App() {
  const [authView, setAuthView] = useState("login");
  const [user, setUser] = useState(null);

  if (user) {
    return <Dashboard onLogout={() => setUser(null)} user={user} />;
  }

  return (
    <main className="auth-page">
      <section className="auth-hero">
        <a className="auth-brand" href="/">
          <span>F</span>
          FinSight
        </a>
        <div>
          <p className="auth-kicker">Finance tracker</p>
          <h2>One clean workspace for your financial life.</h2>
          <p>
            Monitor expenses, savings, assets, and alerts from a dashboard built
            for quick daily decisions.
          </p>
        </div>

        <div className="auth-proof">
          <div>
            <strong>₹2.1L</strong>
            <span>net worth tracked</span>
          </div>
          <div>
            <strong>72/100</strong>
            <span>health score</span>
          </div>
        </div>
      </section>

      {authView === "login" ? (
        <LoginPage onLogin={setUser} onSwitch={() => setAuthView("signup")} />
      ) : (
        <SignupPage onSignup={setUser} onSwitch={() => setAuthView("login")} />
      )}
    </main>
  );
}