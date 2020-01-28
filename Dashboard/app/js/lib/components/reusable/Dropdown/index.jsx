import React, { createRef } from 'react';
import PropTypes from 'prop-types';
import './styles.scss';

export default class Dropdown extends React.Component {
  state = {
    showMenu: false,
  };

  dropdownMenu = createRef();

  showMenu = e => {
    e.preventDefault();
    this.setState({ showMenu: true }, () => {
      document.addEventListener('click', this.closeMenu);
    });
  };

  closeMenu = e => {
    e.preventDefault();

    if (!this.dropdownMenu.current.contains(e.target)) {
      this.setState({ showMenu: false }, () => {
        document.removeEventListener('click', this.closeMenu);
      });
    }
  };

  render() {
    return (
      <div className="dd-wrapper">
        <button onClick={this.showMenu} type="button" className="dropbtn">
          {this.props.title}
          <i className="fa fa-chevron-down" />
        </button>

        {this.state.showMenu && (
          <div className="dd-menu" ref={this.dropdownMenu}>
            {this.props.children(this.closeMenu)}
          </div>
        )}
      </div>
    );
  }
}

Dropdown.propTypes = {
  title: PropTypes.string.isRequired,
  children: PropTypes.any.isRequired,
};
