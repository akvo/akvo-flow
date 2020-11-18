import React from 'react';

export default class OfflineIndicator extends React.Component {
  state = {
    isOffline: false,
  };

  componentDidMount() {
    this.timer = setInterval(() => {
      fetch('https://www.google.com/generate_204', { mode: 'no-cors' })
        .then(res => {
          // with no-cors, res.status is 0 because the javascript ignores all response.
          // we don't need the response, all we need to know is if the request went through or not
          if (res.status !== 204) {
            this.setState({ isOffline: false });
          }
        })
        .catch(() => {
          this.setState({ isOffline: true });
        });
    }, 5000);
  }

  componentWillUnmount() {
    clearInterval(this.timer);
  }

  render() {
    return this.state.isOffline && <p>You are offline. Changes {"won't"} be saved</p>;
  }
}
