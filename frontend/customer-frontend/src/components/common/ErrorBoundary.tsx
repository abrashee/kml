// src / components /ErrorBoundary.tsx
import React from "react";

type Props = {
  children: React.ReactNode;
};

type State = {
  hasError: boolean;
  error?: Error;
};

export default class ErrorBoundary extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): State {
    return {
      hasError: true,
      error,
    };
  }

  componentDidCatch(error: Error, info: React.ErrorInfo) {
    console.error("UI Crash:", error, info);
  }

  reset = () => {
    this.setState({ hasError: false, error: undefined });
  };

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-screen">
          <div className="error-card">
            <h2>Something went wrong</h2>

            <p className="error-msg">
              {this.state.error?.message ?? "Unknown error"}
            </p>

            <button onClick={this.reset}>Retry</button>
            <button onClick={() => (window.location.href = "/")}>
              Go Home
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}