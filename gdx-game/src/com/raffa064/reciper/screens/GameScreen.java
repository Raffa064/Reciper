package com.raffa064.reciper.screens;

import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.reciper.screens.GameScreen.Reciper.Item;
import com.raffa064.reciper.screens.GameScreen.Reciper.Manager;
import com.raffa064.reciper.screens.GameScreen.Reciper.MergeRules;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

public class GameScreen extends InputAdapter implements Screen {
	public SpriteBatch batch;
	public Texture bg;
	public float itemSize = 64 * 2;
	public Item selectedItem;
	public Manager manager = new Manager();
	public MergeRules mergeRules = manager.mergeRules;

	public boolean isColiding(float x, float y, Item i) {
		return 
			x > i.pos.x - itemSize / 2 && x < i.pos.x + itemSize / 2 &&
			y > i.pos.y - itemSize / 2 && y < i.pos.y + itemSize / 2;
	}

	public boolean isColiding(Item i, Item ii) {
		float x = i.pos.x - itemSize / 2;
		float y = i.pos.y - itemSize / 2;
		float x2 = ii.pos.x - itemSize / 2;
		float y2 = ii.pos.y - itemSize / 2;
		return 
			(x + itemSize > x2 && x < x2 + itemSize) &&
			(y + itemSize > y2 && y < y2 + itemSize);
	}

	public void addItem(Item i) {
		manager.items.add(i);
	}

	public void additems(Item... items) {
		for (Item i : items) {
			addItem(i);
		}
	}

	public void removeItem(Item i) {
		manager.trash.add(i);
	}

	public void removeItems(Item... items) {
		for (Item i : items) {
			removeItem(i);
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (selectedItem != null) return false;

		for (int i = manager.items.size() - 1; i >= 0; i--) {
			Item item = manager.items.get(i);
			float fixedY = Gdx.graphics.getHeight() - screenY;
			if (isColiding(screenX, fixedY, item)) {
				selectedItem = item;
				break;
			}
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (selectedItem == null) return false;

		for (int i = manager.items.size() - 1; i >= 0; i--) {
			Item item = manager.items.get(i);
			float fixedY = Gdx.graphics.getHeight() - screenY;
			if (item != selectedItem && isColiding(screenX, fixedY, item)) {
				Item merge = mergeRules.merge(item, selectedItem);
				if (merge != null) {
					merge.pos.set(selectedItem.pos);
					removeItems(item, selectedItem);
					addItem(merge);
					break;
				}
			}
		}

		selectedItem = null;
		return true;
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		bg = new Texture("bg.jpg");
		Gdx.input.setInputProcessor(this);
		String readString = Gdx.files.internal("rules.txt").readString();
		manager.load(readString);

		float index = 0;
		float total = 8 + 1;
		Object[][] debug = {
			{ "Fire", ++index * (1 / total), 0.5f, 20 },
			{ "Sand", ++index * (1 / total), 0.5f, 20 },
			{ "Water", ++index * (1 / total), 0.5f, 20 },
			{ "Milk", ++index * (1 / total), 0.5f, 20 },
			{ "CoffeeBean", ++index * (1 / total), 0.5f, 20 },
			{ "Orange", ++index * (1 / total), 0.5f, 20 },
			{ "Strawbarry", ++index * (1 / total), 0.5f, 20 },
			{ "Banana", ++index * (1 / total), 0.5f, 20 },
		};

		for (Object[] dbg : debug) {
			String itemName = (String) dbg[0];
			float x = (float) dbg[1];
			float y = (float) dbg[2];
			x = x * Gdx.graphics.getWidth();
			y = y * Gdx.graphics.getHeight();
			int amount = (int) dbg[3];

			for (int i = 0; i < amount; i++) {
				Item item = manager.createItem(itemName);
				if (item != null) {
					item.pos.set(x, y);
					addItem(item);
				}
			}
		}
	}

	@Override
	public void render(float p1) {
		dragSelectedItem();

		batch.begin();
		renderBackground();
		renderItens();
		batch.end();

		manager.clearTrash();
	}

	private void renderBackground() {
		float W = Gdx.graphics.getWidth();
		float H = Gdx.graphics.getHeight();
		float s = Math.max(W, H);
		batch.setColor(0.2f, 0.2f, 0.2f, 1);
		batch.draw(bg, W / 2 - s / 2, H / 2 - s / 2, s, s);
		batch.setColor(Color.WHITE);
	}

	public void renderItens() {
		for (Item item : manager.items) {
			batch.draw(item.texture, item.pos.x - itemSize / 2, item.pos.y - itemSize / 2, itemSize, itemSize);
		}
	}

	public void dragSelectedItem() {
		if (selectedItem != null) {
			float fixedY = Gdx.graphics.getHeight() - Gdx.input.getY();
			selectedItem.pos.set(Gdx.input.getX(), fixedY);
		}
	}

	@Override
	public void resize(int p1, int p2) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

	public static class Reciper {
		public static class Item {
			public String name;
			public Texture texture;
			public Vector2 pos = new Vector2();

			public Item(String name, Texture texture) {
				this.name = name;
				this.texture = texture;
			}

			public Item clone() {
				Item item = new Item(name, texture);
				return item;
			}
		}

		public static class DynamicJavascript {
			public Context ctx;
			public ScriptableObject scope;
			public Manager manager;

			public DynamicJavascript(Manager manager) {
				this.manager = manager;
				ctx = Context.enter();
				ctx.setOptimizationLevel(-1);
				scope = ctx.initStandardObjects();
				scope.put("dynamic", scope, new DynamicManager(ctx, scope, manager));
			}

			public void exec(String script) {
				ctx.compileString(script, "script.js", 0, null).exec(ctx, scope);
			}

			public static class DynamicManager {
				public Context ctx;
				public ScriptableObject scope;
				public Manager manager;

				public DynamicManager(Context ctx, ScriptableObject scope, Manager manager) {
					this.ctx = ctx;
					this.scope = scope;
					this.manager = manager;
				}
				

				public Object getItemNames() {
					List<String> names = new ArrayList<>();
					for (Map.Entry<String, Item> item : manager.templateItems.entrySet()) {
						names.add(item.getKey());
					}
					return Context.javaToJS(names, scope);
				}
				
				public void log(String log) {
					Log.d("com.raffa064.recipe", log);
				}

				public void createItem(String name, String texturePath) {
					Item item = new Item(name, manager.loadTexture(texturePath));
					manager.templateItems.put(name, item);
				}

				public void createRule(String result, String a, String b) {
					manager.mergeRules.addRule(result, a, b);
				}
			}
		}

		public static class Manager {
			public DynamicJavascript dinamicJavascript;
			public List<Item> items = new ArrayList<>();
			public List<Item> trash = new ArrayList<>();
			public MergeRules mergeRules = new MergeRules(this);
			public HashMap<String, Texture> assets = new HashMap<>();
			public HashMap<String, Item> templateItems = new HashMap<>();

			public Item createItem(String name) {
				Item item = templateItems.getOrDefault(name, null);
				if (item != null) {
					return item.clone();
				}
				return null;
			}

			public void clearTrash() {
				for (Item i : trash) {
					items.remove(i);
				}
				trash.clear();
			}

			public void load(String rules) {
				String dinamicScript = "";
				boolean readingItems = false;
				boolean readingDinamic = false;

				for (String line : rules.split("\n")) {
					line = line.trim();
					if (line.startsWith("#")) { //Configuration prefix
						readingItems = false;
						readingDinamic = false;

						if (line.toLowerCase().equals("#items:")) {
							readingItems = true;
						}

						if (line.toLowerCase().equals("#dynamic:")) {
							readingDinamic = true;
						}
					} else {
						if (readingItems) { //Reading rule
							if (line.matches("[a-zA-Z0-9\\-_]{1,}\\s*:\\s*[a-zA-Z0-9\\-_\\./]{1,}")) { //name: texture
								int colon = line.indexOf(":");

								String name = line.substring(0, colon).trim();
								String texturePath = line.substring(colon + 1, line.length()).trim();

								Item item = new Item(name, loadTexture(texturePath));
								templateItems.put(name, item);
							}
						} 

						if (readingDinamic) {
							dinamicScript += line + "\n";
						}
					}
				}

				dinamicJavascript = new DynamicJavascript(this);
				dinamicJavascript.exec(dinamicScript);

				mergeRules.load(rules);
			}

			public Texture loadTexture(String texturePath) {
				if (assets.containsKey(texturePath)) {
					return assets.get(texturePath);
				}

				Texture texture = new Texture(texturePath); //TODO: complete load texture logics
				assets.put(texturePath, texture);

				return texture; 
			}

		}

		public static class MergeRules {
			public Manager manager;
			public List<Rule> rules = new ArrayList<>();

			public MergeRules(Manager manager) {
				this.manager = manager;
			}

			public void addRule(String name,  String a, String b) {
				Rule rule = new Rule(name, a, b);
				rules.add(rule);
			}

			public void load(String rules) {
				boolean reading = false;

				for (String line : rules.split("\n")) {
					line = line.trim();
					if (line.startsWith("#")) { //Configuration prefix
						if (line.toLowerCase().equals("#rules:")) {
							reading = true;
						} else {
							reading = false;
						}
					} else if (reading) { //Reading rule
						if (line.matches("[a-zA-Z0-9\\-_]{1,}\\s*:\\s*[a-zA-Z0-9\\-_]{1,}\\s*,\\s*[a-zA-Z0-9\\-_]{1,}")) { //name: a, b
							int colon = line.indexOf(":");
							int comma = line.indexOf(",", colon);

							String name = line.substring(0, colon).trim();
							String a = line.substring(colon + 1, comma).trim();
							String b = line.substring(comma + 1, line.length()).trim();

							addRule(name, a, b);
						}
					}
				}
			}

			public Item merge(Item a, Item b) {
				for (Rule rule : rules) {
					boolean match = rule.a.equals(a.name) && rule.b.equals(b.name);
					boolean inverseMatch = rule.b.equals(a.name) && rule.a.equals(b.name);
					if (match || inverseMatch) {
						return manager.createItem(rule.result); 
					}
				}
				return null;
			}

			public static class Rule {
				public String result, a, b; //a + b = result

				public Rule(String result, String a, String b) {
					this.result = result;
					this.a = a;
					this.b = b;
				} 
			}
		}
	}
}
