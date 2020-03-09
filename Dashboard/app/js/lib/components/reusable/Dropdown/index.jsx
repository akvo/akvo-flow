import React, { createRef } from 'react';
import PropTypes from 'prop-types';
import './styles.scss';

export class Dropdown extends React.Component {
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
    this.setState({ showMenu: false }, () => {
      document.removeEventListener('click', this.closeMenu);
    });
  };

  render() {
    return (
      <div className="dd-wrapper">
        <button
          disabled={this.props.disabled}
          onClick={this.showMenu}
          type="button"
          className="dropbtn"
        >
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

export function DropdownItem(props) {
  const onClick = e => {
    props.closeMenu(e);
    props.onClick();
  };

  return (
    <button onClick={onClick} type="button">
      {props.children}
    </button>
  );
}

Dropdown.propTypes = {
  title: PropTypes.string.isRequired,
  disabled: PropTypes.bool.isRequired,
  children: PropTypes.any.isRequired,
};

DropdownItem.propTypes = {
  onClick: PropTypes.func.isRequired,
  closeMenu: PropTypes.func.isRequired,
  children: PropTypes.any.isRequired,
};
