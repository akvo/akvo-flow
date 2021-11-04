import React, { useContext } from 'react';
import DevicesTabContext from '../devices-context';

export default function DeleteGroup() {
  const { isShowDeleteDialog, cancelDeletingGroup, deleteGroupConfirm, strings } = useContext(
    DevicesTabContext
  );

  return (
    <div className={isShowDeleteDialog ? 'overlay display' : 'overlay'}>
      <div className="blanket" />
      <div className="dialogWrap">
        <div className="confirmDialog dialog">
          <h2>device group delete header</h2>
          <p className="dialogMsg">this cant be undo</p>
          <div className="buttons menuCentre">
            <ul>
              <li>
                <button type="button" className="ok smallBtn" onClick={deleteGroupConfirm}>
                  {strings.dialogText.save}
                </button>
              </li>
              <li>
                <button type="button" className="cancel" onClick={cancelDeletingGroup}>
                  {strings.dialogText.cancel}
                </button>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
