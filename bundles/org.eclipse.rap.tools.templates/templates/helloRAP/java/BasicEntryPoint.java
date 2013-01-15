package $packageName$;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class BasicEntryPoint extends AbstractEntryPoint {

  @Override
  protected void createContents( Composite parent ) {
      parent.setLayout( new FormLayout() );
      Color backgroundColor = new Color( parent.getDisplay(), 0x31, 0x61, 0x9C );
      Composite header = new Composite( parent, SWT.NONE );
      header.setBackground( backgroundColor );
      header.setBackgroundMode( SWT.INHERIT_DEFAULT );
      header.setLayoutData( createLayoutDataForHeader() );
      Label label = new Label( header, SWT.NONE );
      label.setText( "Hello RAP World!" );
      label.setForeground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
      label.setBounds( 40, 30, 250, 30 );
  }

  private FormData createLayoutDataForHeader() {
      FormData layoutData = new FormData();
      layoutData.left = new FormAttachment( 0, 0 );
      layoutData.right = new FormAttachment( 100, 0 );
      layoutData.top = new FormAttachment( 0, 0 );
      layoutData.height = 80;
      return layoutData;
  }

}
