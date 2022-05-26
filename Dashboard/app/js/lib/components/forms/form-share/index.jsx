/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import Modal from 'akvo-flow/components/reusable/Modal';
import { trackEvent } from 'akvo-flow/analytics';
import './style.scss';

export default class WebFormShare extends React.Component {
  state = {
    modalOpen: false,
    copyButtonText: 'Copy link',
    copyButtonV2Text: 'Copy link',
  };

  toggleModal = () => {
    const { modalOpen } = this.state;
    this.setState({ modalOpen: !modalOpen });
  };

  copyToClipboard = (event) => {
    if (event.target.id === 'copy-link-v1') {
        navigator.clipboard.writeText(this.props.data.shareUrl);
        this.setState({ copyButtonText: 'Copied!'});
    } else {
        navigator.clipboard.writeText(this.props.data.shareUrlV2);
        this.setState({ copyButtonV2Text: 'Copied!'});
    }
    setTimeout(() => { this.setState({ copyButtonText: 'Copy link', copyButtonV2Text: 'Copy link'}) }, 900);
    trackEvent('Webform URL copied');
  };

  openModal = () => {
    if (!this.props.data.valid) {
      return;
    }

    this.toggleModal();
    this.props.actions.getShareURL();
  };

  render() {
    const { valid, shareUrl, shareUrlV2, showWebFormV2 } = this.props.data;
    return (
      <>
        <li>
          <a
            onClick={this.openModal}
            href="#"
            className={`previewNewSurvey ${valid ? '' : 'disabled'}`}
          >
            Share as a webform
          </a>
        </li>

        <Modal isOpen={this.state.modalOpen} toggleModal={this.toggleModal} id="form-share-modal">
            <div className="modal-header">
            <h3>Share all questions as a webform</h3>

            <i
                className="fa fa-times icon"
                onClick={this.toggleModal}
                onKeyDown={this.toggleModal}
            />
            </div>

            <div className="modal-body">
                <div className="form-link">
                {shareUrl ? (
                    <div className="link">
                        <span>{shareUrl}</span>
                        <span className="version">v1</span>
                        <button id="copy-link-v1" onClick={this.copyToClipboard}>
                            {this.state.copyButtonText}
                        </button>
                    </div>
                ) : (
                    <p>Loading URL.....</p>
                )}
                </div>
                {showWebFormV2 &&
                    (<div className="form-link">
                    {shareUrlV2 ? (
                        <div className="link">
                            <span>{shareUrlV2}</span>
                            <span className="version">v2</span>
                            <button id="copy-link-v2" onClick={this.copyToClipboard}>
                                {this.state.copyButtonV2Text}
                            </button>
                        </div>
                    ) : (
                        <p>Loading URL.....</p>
                    )}
                    </div>)}
                <div className="password">
                    <span>Password: webform</span>
                </div>

                <div className="action-button">
                    <button onClick={this.toggleModal} type="button" className="button">
                        Done
                    </button>
                </div>
            </div>
        </Modal>
      </>
    );
  }
}

WebFormShare.propTypes = {
  strings: PropTypes.object.isRequired,
  data: PropTypes.object.isRequired,
  actions: PropTypes.object.isRequired,
};
