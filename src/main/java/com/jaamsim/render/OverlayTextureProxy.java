package com.jaamsim.render;

import java.net.URL;
import java.util.ArrayList;

public class OverlayTextureProxy implements RenderProxy {

	private int _x, _y;
	private int _width, _height;

	private URL _imageURL;

	private boolean _isTransparent;
	private boolean _isCompressed;
	private boolean _alignRight, _alignBottom;
	private ArrayList<Integer> _visibleWindowIDs;

	public OverlayTextureProxy(int x, int y, int width, int height, URL imageURL, boolean transparent, boolean compressed,
	                           boolean alignRight, boolean alignBottom, ArrayList<Integer> visibleWindowIDs) {
		_x = x; _y = y;
		_width = width; _height = height;
		_imageURL = imageURL;
		_isTransparent = transparent; _isCompressed = compressed;
		_alignRight = alignRight; _alignBottom = alignBottom;
		_visibleWindowIDs = visibleWindowIDs;
	}

	@Override
	public void collectRenderables(Renderer r, ArrayList<Renderable> outList) {
		return;
	}

	@Override
	public void collectOverlayRenderables(Renderer r,
			ArrayList<OverlayRenderable> outList) {
		outList.add(new OverlayTexture(_x, _y, _width, _height, _imageURL, _isTransparent, _isCompressed,
		            _alignRight, _alignBottom, _visibleWindowIDs));
	}

}