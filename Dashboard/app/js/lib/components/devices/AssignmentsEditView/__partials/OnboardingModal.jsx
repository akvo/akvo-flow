import React from 'react';
import PropTypes from 'prop-types';
import ReactModal from 'react-modal';

// ReactModal.setAppElement('#assignSurveys');

export default class OnboardingModal extends React.Component {
  state = {
    currentOnboardingMessageIdx: 0,
  };

  onBoardingMessages = strings => [
    {
      title: strings.datapointAssignment,
      message: strings.datapointAssignmentInfo,
    },
    {
      title: strings.formSelection,
      message: strings.formSelectionInfo,
    },
  ];

  changeOnboardingMessage = messageIdx => {
    this.setState({ currentOnboardingMessageIdx: messageIdx });
  };

  render() {
    const onBoardingMessages = this.onBoardingMessages(this.props.strings);
    const { currentOnboardingMessageIdx } = this.state;
    const isFirstItem = currentOnboardingMessageIdx === 0;
    const isLastItem = currentOnboardingMessageIdx === onBoardingMessages.length - 1;

    return (
      <ReactModal
        isOpen={this.props.isOpen}
        onRequestClose={this.props.toggleModal}
        id={this.props.id}
        ariaHideApp={false}
        overlayClassName="modal-overlay"
        className="modal-container"
      >
        <header className="modal-header">
          <h2>{this.props.strings.announcingChangesInAssignments}</h2>
          <i
            className="fa fa-times close-icon"
            onClick={this.props.toggleModal}
            onKeyDown={this.props.toggleModal}
          />
        </header>

        <section className="modal-body">
          <p className="section-header">{onBoardingMessages[currentOnboardingMessageIdx].title}</p>

          <p>{onBoardingMessages[currentOnboardingMessageIdx].message}</p>
        </section>

        <footer className="modal-footer">
          <div className="back-action">
            {!isFirstItem && (
              <button
                type="button"
                onClick={() => this.changeOnboardingMessage(currentOnboardingMessageIdx - 1)}
              >
                {this.props.strings.back}
              </button>
            )}
          </div>

          <div className="section-list">
            <ul>
              {Array.from({ length: onBoardingMessages.length }, (_, idx) => (
                <li
                  className={idx === currentOnboardingMessageIdx ? 'active' : undefined}
                  key={idx}
                >
                  <span className="dot" />
                </li>
              ))}
            </ul>
          </div>

          <div className="next-action">
            <button
              type="button"
              onClick={
                isLastItem
                  ? this.props.toggleModal
                  : () => this.changeOnboardingMessage(currentOnboardingMessageIdx + 1)
              }
            >
              {isLastItem ? this.props.strings.okay : this.props.strings.next}
            </button>
          </div>
        </footer>
      </ReactModal>
    );
  }
}

OnboardingModal.propTypes = {
  id: PropTypes.string.isRequired,
  isOpen: PropTypes.bool.isRequired,
  toggleModal: PropTypes.func.isRequired,
  strings: PropTypes.object.isRequired,
};
